package com.jgitfx.jgitfx;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;

import java.io.File;

/**
 * GitActions provides static methods for doing some of the high-porcelain level commands without needing to interact
 * directly with JGit.
 */
public class GitActions {

    /**
     * Creates a new repository and returns it as a {@link Git} object. Note: this uses a static factory
     * class to construct the {@code Git} object, so calling {@link Git#close()} on it should also
     * close the underlying repository. See {@link Git#close()} for more details relating to this.
     *
     * @param parentDirectory the repository's parent directory
     * @return the repository as a high-porcelain {@link Git} object
     * @throws GitAPIException
     */
    public static Git createRepoIn(File parentDirectory) throws GitAPIException {
        return Git.init().setDirectory(parentDirectory).call();
    }

    /**
     * Opens a repository as a {@link Git} object from the given ".git" repo. Note: this uses a static factory
     * class to construct the {@code Git} object, so calling {@link Git#close()} on it should also
     * close the underlying repository. See {@link Git#close()} for more details relating to this.
     *
     * @param gitRepoDir the ".git" repo directory
     * @return the repository as a high-porcelain {@link Git} object
     * @throws GitAPIException
     */
    public static Git openRepo(File gitRepoDir) throws GitAPIException {
        return Git.init().setGitDir(gitRepoDir).call();
    }

}
