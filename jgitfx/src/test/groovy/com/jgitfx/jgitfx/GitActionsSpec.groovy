package com.jgitfx.jgitfx

import org.eclipse.jgit.api.Git
import org.eclipse.jgit.api.Status
import org.eclipse.jgit.api.errors.RefNotFoundException
import org.eclipse.jgit.lib.PersonIdent
import org.eclipse.jgit.lib.Ref
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Stepwise

@Stepwise
class GitActionsSpec extends Specification {

    @Shared
    File rootDir

    @Shared
    File file

    @Shared
    String relativePath

    @Shared
    Git git

    @Shared
    PersonIdent fakeAuthor

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
                .setMessage("initial commit: add main file to repository")
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

    def cleanupSpec() {
        // clean up memory
        git.close()

        // delete test repo dir
        rootDir.deleteDir()
    }

}