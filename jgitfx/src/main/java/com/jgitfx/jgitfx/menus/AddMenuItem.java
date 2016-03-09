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
 * A {@link MenuItem} that will add the untracked file(s) to the git repository.
 * <p>Note: this object is intended to be added to a {@link javafx.scene.control.ContextMenu} that is displayed
 * when a user right-clicks a row (presumably representing some file) in a TreeView; it is not intended to be
 * added to a {@link javafx.scene.control.Menu} though that is still possible.
 */
public abstract class AddMenuItem extends MenuItem {

    /**
     * Constructs a MenuItem that will add the untracked files to the git repository.
     *
     * @param text the MenuItem's text
     * @param graphic the graphic to display besides the MenuItem
     * @param filePatternGetter the list of the relative path(s) from the root directory of the files to add/stage.
     */
    public AddMenuItem(String text, Node graphic, Val<Git> git, Supplier<List<String>> filePatternGetter) {
        super(text, graphic);
        setOnAction(ae -> {
            try {
                AddCommand addCmd = git.getOrThrow().add();
                // insure add command will add newly staged files
                addCmd.setUpdate(false);
                filePatternGetter.get().forEach(addCmd::addFilepattern);
                addCmd.call();
            } catch (GitAPIException e) {
                e.printStackTrace();
            }
        });

    }

    /**
     * Constructs an AddMenuItem with the specified text and no graphic.
     */
    public AddMenuItem(String text, Val<Git> git, Supplier<List<String>> filePatternGetter) {
        this(text, null, git, filePatternGetter);
    }

    /**
     * Constructs an AddMenuItem with "Add File(s)..." as its text and no graphic.
     */
    public AddMenuItem(Val<Git> git, Supplier<List<String>> filePatternGetter) {
        this("", git, filePatternGetter);
    }
}
