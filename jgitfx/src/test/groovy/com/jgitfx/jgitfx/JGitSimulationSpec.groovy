package com.jgitfx.jgitfx

import org.eclipse.jgit.api.Git
import org.eclipse.jgit.api.Status
import org.eclipse.jgit.api.errors.RefNotFoundException
import org.eclipse.jgit.lib.PersonIdent
import org.eclipse.jgit.lib.Ref
import org.eclipse.jgit.revwalk.RevCommit
import spock.lang.Narrative
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Stepwise
import spock.lang.Title

@Narrative("""
JGitSimulationSpec uses Spock Tests to demonstrate how to use JGit correctly in a stream-lined fashion.
Rather than asking, "How do I do [some Git command/action]?" I'm asking, "How do I simulate
a real-world user experience of using JGit?" For example, a user might do the following:
Create a repository, make some commit, make changes to the file(s),
make more commits, call the 'log' command to see past revisions,
change the file(s), stash the changes and check out some other commit and reapply the stash, etc.

@Stepwise is used to insure that methods are executed in order. Thus, by reading through this
Spock test (and in my case, designing it), one can come to understand how to use JGit's
high porcelain commands and know how to get the mid-level arguments needed for some of its methods
(e.g. RevCommit, Ref, etc.)
""")
@Title("Spec for demonstrating JGit in simulated real-world user experience")
@Stepwise
class JGitSimulationSpec extends Specification {

    @Shared
    File rootDir    // the directory that stores the Git meta-directory and all tracked files/directories

    @Shared
    File file1       // A file used to test how to make changes, merges, etc.

    @Shared
    File file2       // A file used to test how to make changes, merges, etc.

    @Shared
    Git git         // Allow high porcelain git commands

    @Shared
    PersonIdent fakeAuthor  // temporary author to use until figure out how to get authors/committers

    def setupSpec() {
        rootDir = new File(System.getProperty("user.home"), "TestRepoDir")
        rootDir.mkdir()

        file1 = new File(rootDir, "file1.txt")
        file1.createNewFile()

        file2 = new File(rootDir, "file2.txt")
        file2.createNewFile()

        // Rather than using @Shared Strings to store the relative path of a file,
        //  just add a method that returns the correct String

        // "path/to/user/home/file#.txt" (absolute Path) --> "file#.txt" (relative path)
        File.metaClass.getRelativePath = {
            // get relative path
            String relative = delegate.path - delegate.parent

            // still need to remove the file1 separator
            // "/file#.txt" --> "file#.txt"
            return relative.substring(1)
        }

        fakeAuthor = new PersonIdent("author", "address@domain.com")

        // create a new repository
        git = Git.init().setDirectory(rootDir).call()
    }

    def "new repository has no branches and attempting to create one fails"() {
        given: "branch list"
        List<Ref> branchList = git.branchList().call()

        expect: "branch list to be empty"
        branchList.isEmpty()

        when: "attempt to create a 'master' branch"
        git.branchCreate().setName("master").call()

        then: "that throws a RefNotFoundException: Cannot resolve HEAD"
        thrown(RefNotFoundException)
    }

    def "Making initial commit will indirectly create a 'master' branch"() {
        setup: "when status is called when directory has a new file1 and repo has no commits"
        Status status = git.status().call()

        expect: "file1 is listed as 'untracked'"
        status.untracked[0] == file1.getRelativePath()
        status.untracked[1] == file2.getRelativePath()

        when: "file1 is added"
        git.add()
                .addFilepattern(file1.getRelativePath())
                .addFilepattern(file2.getRelativePath()).call()

        and: "status is requested"
        status = git.status().call()

        then: "file1 is listed as 'new'"
        status.added[0] == file1.getRelativePath()
        status.added[1] == file2.getRelativePath()

        when: "file1 is committed"
        git.commit()
                .setAuthor(fakeAuthor)
                .setMessage("initial commit" + "\n\n" + "Added files to repository")
                .call()

        and: "status is requested"
        status = git.status().call()

        then: "there are no uncommitted changes"
        !status.hasUncommittedChanges()
        status.added.isEmpty()

        and: "a 'master' branch has been created indirectly"
        git.repository.findRef("master") != null
    }

    def "Attempt to create a new branch will now succeed"() {
        when: "try to create a new branch called 'secondBranch'"
        git.branchCreate().setName("secondBranch").call()

        then: "that branch now exists"
        git.repository.findRef("secondBranch") != null

        when: "try to create a new branch called 'thirdBranch' using checkout method"
        git.branchCreate().setName("thirdBranch").call()

        then: "that branch now exists"
        git.repository.findRef("thirdBranch") != null
    }

    def "Delete previous method's branches and re-checkout 'master'"() {
        setup: "delete previous branches"
        git.branchDelete().setBranchNames("secondBranch", "thirdBranch").call()

        expect: "only one branch to be listed"
        git.branchList().call().size() == 1

        cleanup: "checkout master"
        git.checkout().setName("master").call()
    }

    def "Make changes to both files but only commit one of the files (selective commit)"() {
        setup: "get the number of commits files have had on them"
        int firstCount_File1 = git.log().addPath(file1.getRelativePath()).call().size()
        int firstCount_File2 = git.log().addPath(file2.getRelativePath()).call().size()

        and: "change both files"
        file1.text = "a new change!"
        file2.text = "this guy's new"

        expect: "status to list both files as modified"
        git.status().call().modified.size() == 2

        when: "add and commit file 2"
        git.add().addFilepattern(file2.getRelativePath()).call()
        git.commit().setMessage("only committing file 2").setAuthor(fakeAuthor).call()

        and: "get the number of commits files have had on them again"
        int secondCount_File1 = git.log().addPath(file1.getRelativePath()).call().size()
        int secondCount_File2 = git.log().addPath(file2.getRelativePath()).call().size()

        then: "file 1's second count should be the same as original"
        secondCount_File1 == firstCount_File1

        and: "file 2's second count has one more commit than its original"
        secondCount_File2 == firstCount_File2 + 1
    }

    def "Look at the log of a repository"() {
        setup: "Clear file1's text"
        file1.text = ""

        and: "do 5 commits, each of which adds 'Line: #' to file1's text"
        5.times { idx ->
            file1.text += "Line: $idx"
            assert git.status().call().hasUncommittedChanges()
            // no need to add the file1 since it's already tracked and added by default
            // git.add().addFilepattern(file1.getRelativePath()).call()
            git.commit()
                    .setAuthor(fakeAuthor)
                    .setMessage("Adding line $idx" + "\n\n" + "Appended a line to the file1")
                    .call()
        }

        when: "log all the commits"
        Iterable<RevCommit> logList = git.log().all().call()

        then: "print out those commits"
        println "\n\nPrinting the log list\n"
        logList.each { RevCommit revCommit ->
            revCommit.with {
                println "Name is: $name"
                println "Commit time is: ${new Date(commitTime * 1000l).toString()}"
                println "Author is [${authorIdent.toString()}]"
                println "First line is [$shortMessage]"
                println "Full message is [$fullMessage]"
                println "Tree is:"
                println "\tName - ${tree.name}"
                println "\tObjectID name - ${tree.getId().name}"
                println "Number of parents: $parentCount"
                parents.eachWithIndex { parent, idx ->
                    println "Parent #$idx: ${parent.name}"
                }
                println "============"
            }
        }
        println "\nFinished printing log list"
    }

    def cleanupSpec() {
        // clean up memory
        git.close()

        // delete test repo dir
        rootDir.deleteDir()
    }

}