package com.jgitfx.jgitfx.menus;

import javafx.scene.Node;
import javafx.scene.control.MenuItem;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;

import java.io.File;

/**
 * A {@link MenuItem} with a method that will create a new repository and
 * return its output as a {@link Git} object.
 *
 * <p>To add this functionality, use</p>
 * <pre>
 *     {@code
 *     CreateRepoMenuItem crmItem = // creation code;
 *     crmItem.setOnAction(ae -> {
 *        // code to get parent directory (probably via DirChooser
 *        File parentDir = // gets parent directory
 *
 *        Git git = crmItem.createGitRepo(parentDir);
 *
 *        // any other code (if needed)...
 *     });
 *     }
 * </pre>
 */
public class CreateRepoMenuItem extends MenuItem {

    public CreateRepoMenuItem(String text, Node graphic) {
        super(text, graphic);
    }

    /**
     * @param parentDirectory the directory in which the ".git" meta-directory should be. For example,
     *                        assuming an absolute path of "/home/user/parentDirectory/", the git repository
     *                        will be created with the path "/home/user/parentDirectory/.git/"
     * @return the created {@link Git} object, or {@code null} if an error occurs.
     */
    public Git createGitRepo(File parentDirectory) {
        try {
            return Git.init().setDirectory(parentDirectory).call();
        } catch (GitAPIException e) {
            handleGitAPIException(e);
            return null;
        }
    }

    /**
     * If a {@link GitAPIException} is thrown, a developer can handle it here. Defaults to printing out stacktrace.
     * @param e the exception that might be thrown from {@link #createGitRepo(File)}
     */
    protected void handleGitAPIException(GitAPIException e) {
        e.printStackTrace();
    }

}
