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
Create a repository, make some commit, make changes to the file,
make more commits, call the 'log' command to see past revisions,
change the file, stash the changes and check out some other commit and reapply the stash, etc.

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
    File file       // the main file used to test how to make changes, merges, etc.

    @Shared
    String relativePath // the relative path of file from rootDir to file: "file.txt"

    @Shared
    Git git         // Allow high porcelain git commands

    @Shared
    PersonIdent fakeAuthor  // temporary author to use until figure out how to get authors/committers

    def setupSpec() {
        rootDir = new File(System.getProperty("user.home"), "TestRepoDir")
        rootDir.mkdir()

        file = new File(rootDir, "file.txt")
        file.createNewFile()

        // get relative file path
        String relative = file.path - file.parent

        // still need to remove the file separator
        // "/file.txt" --> "file.txt"
        relativePath = relative.substring(1)

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
        setup: "when status is called when directory has a new file and repo has no commits"
        Status status = git.status().call()

        expect: "file is listed as 'untracked'"
        status.untracked[0] == relativePath

        when: "file is added"
        git.add().addFilepattern(relativePath).call()

        and: "status is requested"
        status = git.status().call()

        then: "file is listed as 'new'"
        status.untracked.isEmpty()
        status.added[0] == relativePath

        when: "file is committed"
        git.commit()
                .setAuthor(fakeAuthor)
                .setMessage("initial commit" + "\n\n" + "Added main file to repository")
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

    def "Look at the log of a repository"() {
        setup: "Clear the file's text"
        file.text = ""

        and: "do 5 commits, each of which adds 'Line: #' to file's text"
        5.times { idx ->
            file.text += "Line: $idx"
            assert git.status().call().hasUncommittedChanges()
            // no need to add the file since it's already tracked and added by default
            // git.add().addFilepattern(relativePath).call()
            git.commit()
                    .setAuthor(fakeAuthor)
                    .setMessage("Adding line $idx" + "\n\n" + "Appended a line to the file")
                    .call()
        }

        when: "log all the commits"
        Iterable<RevCommit> logList = git.log().all().call()

        then: "print out those commits"
        println "\n\nPrinting the log list\n"
        logList.each {
            it.with {
                println "Name is: $name"

                // bug: new Date(commitTime).toString() prints the epoch date, not the commit date
                println "Commit time is: ${new Date(commitTime).toString()}"

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