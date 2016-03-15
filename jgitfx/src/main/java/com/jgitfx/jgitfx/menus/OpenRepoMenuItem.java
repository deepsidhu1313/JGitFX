package com.jgitfx.jgitfx.menus;

import java.io.File;
import javafx.scene.Node;
import javafx.scene.control.MenuItem;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;

/**
 * A {@link MenuItem} with a method that will open a repository from local storage and return
 * that opened repository as a {@link Git} object.
 *
 * <p>To add the functionality, use</p>
 * <pre>
 *     {@code
 *     OpenRepoMenuItem ormItem = // creation code;
 *     ormItem.setOnAction(ae -> {
 *        // code to get git meta-directory (probably via DirChooser
 *        File gitMetaDir = // gets git meta-directory
 *
 *        Git git = ormItem.openGitRepo(gitMetaDir);
 *
 *        // any other code (if needed)...
 *     });
 *     }
 * </pre>
 */
public class OpenRepoMenuItem extends MenuItem {

    public OpenRepoMenuItem(String text, Node graphic) {
        super(text, graphic);
    }

    /**
     * @param gitMetaDirectory the ".git" directory
     * @return the Git object or null if an error occurs
     */
    public Git openGitRepo(File gitMetaDirectory) {
        try {
            return Git.init().setGitDir(gitMetaDirectory).call();
        } catch (GitAPIException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * If a {@link GitAPIException} is thrown, a developer can handle it here. Defaults to printing out stacktrace.
     * @param e the exception that might be thrown from {@link #openGitRepo(File)}
     */
    protected void handleGitAPIException(GitAPIException e) {
        e.printStackTrace();
    }
}
