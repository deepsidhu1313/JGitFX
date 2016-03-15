package com.jgitfx.jgitfx.menus;

import javafx.scene.Node;
import javafx.scene.control.MenuItem;
import org.eclipse.jgit.api.AddCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.reactfx.value.Val;

import java.util.List;
import java.util.function.Supplier;

/**
 * A {@link MenuItem} with a method that will add the untracked file(s) to the git repository.
 * <p>Note: this object is intended to be added to a {@link javafx.scene.control.ContextMenu} that is displayed
 * when a user right-clicks a row (presumably representing some file) in a TreeView; it is not intended to be
 * added to a {@link javafx.scene.control.Menu} though that is still possible.
 *
 * <p>To add this functionality, use</p>
 * <pre>
 *     {@code
 *     AddMenuItem amItem = // creation code;
 *     amItem.setOnAction(ae -> {
 *        // any necessary code if need be...
 *
 *        amItem.addFiles();
 *
 *        // any necessary code if need be...
 *     });
 *     }
 * </pre>
 */
public abstract class AddMenuItem extends MenuItem {

    private final Val<Git> git;
    private final Supplier<List<String>> filePatternGetter;

    /**
     * Constructs a MenuItem that will add the untracked files to the git repository.
     *
     * @param text the MenuItem's text
     * @param graphic the graphic to display besides the MenuItem
     * @param filePatternGetter the list of the relative path(s) from the root directory of the files to add/stage.
     */
    public AddMenuItem(String text, Node graphic, Val<Git> git, Supplier<List<String>> filePatternGetter) {
        super(text, graphic);
        this.git = git;
        this.filePatternGetter = filePatternGetter;
    }

    /**
     * Adds the files returned from {@link #filePatternGetter} and then calls {@link #updateFiles(List)}
     */
    public final void addFiles() {
        try {
            AddCommand addCmd = git.getOrThrow().add();
            // insure add command will add newly staged files
            addCmd.setUpdate(false);

            // add files
            List<String> files = filePatternGetter.get();
            files.forEach(addCmd::addFilepattern);

            addCmd.call();

            updateFiles(files);
        } catch (GitAPIException e) {
            handleGitAPIException(e);
        }
    }

    /**
     * A method for optionally updating files after they are added to the repository. Called after {@link #addFiles()}.
     * Default implementation does nothing.
     * @param relativePaths the list of file's relative paths as {@code String}s.
     */
    protected void updateFiles(List<String> relativePaths) {}

    /**
     * If a {@link GitAPIException} is thrown, a developer can handle it here. Defaults to printing out stacktrace.
     * @param e the exception that might be thrown from {@link #addFiles()}
     */
    protected void handleGitAPIException(GitAPIException e) {
        e.printStackTrace();
    }
}
