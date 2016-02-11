package com.jgitfx.jgitfx

import org.eclipse.jgit.api.Git
import spock.lang.Specification
import spock.lang.Stepwise

import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths


/**
 * Created by jordan on 2/11/16.
 */
@Stepwise
class GitActionsSpec extends Specification {

    def "CreateRepoInParentDir creates a new '.git' repo in that directory"() {
        given: "Some parent directory..."
        Path parentDirPath = Paths.get(".")
        println "\t\t====Parent Dir Path: ${parentDirPath.toAbsolutePath().toString()}====="
        File parentDir = parentDirPath.toFile()

        and: "...that does not have a git repository"
        Path gitRepoDirPath = parentDirPath.resolve(".git")
        Files.deleteIfExists(gitRepoDirPath)

        when: "Method #createRepoIn is called"
        Git git = GitActions.createRepoIn(parentDir)

        then: "a new repo is created"
        File gitRepoDir = git.getRepository().getDirectory()
        gitRepoDir.exists()

        and: "the repo ends with '.git'"
        gitRepoDir.name.endsWith(".git")

        and: "it is within that parent directory."
        gitRepoDir.getParentFile() == parentDir

        cleanup: "close but do not delete the repo so it can be used for next test"
        git.repository.close()
        git.close()
    }

    def "OpenRepo opens the previously saved '.git' repo"() {
        given: "Some repository..."
        Path gitRepoDirPath = Paths.get(".").resolve(".git")
        File gitRepoDir = gitRepoDirPath.toFile()

        expect: "...that exists"
        Files.exists(gitRepoDirPath)

        when: "the repositor is opened"
        Git git = GitActions.openRepo(gitRepoDir)

        then: "the repository actually exists"
        git.repository.getDirectory() == gitRepoDir

        // when other tests are implemented, remove this cleanup so others can use the sample Git repository
        cleanup: "close but do not delete the repo so it can be used for next test"
        git.repository.close()
        git.close()
        gitRepoDir.deleteDir()
    }
}