package com.jgitfx.jgitfx.menus;

import com.jgitfx.jgitfx.dialogs.RevertChangesDialog;
import com.jgitfx.jgitfx.dialogs.RevertChangesDialogPane;
import com.jgitfx.jgitfx.fileviewers.SelectableFileViewer;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.MenuItem;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.Status;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.reactfx.value.Val;


/**
 * A {@link MenuItem} that shows a {@link RevertChangesDialogPane} when
 * there are uncommitted changes and an alert dialog informing user there are no changes when
 * there are none.
 */
public class RevertChangesMenuItem extends MenuItem {

    public RevertChangesMenuItem(String text, Node graphic, Val<Git> git) {
        super(text, graphic);
        setOnAction(ae -> {
            try {
                Status status = git.getOrThrow().status().call();
                if (status.hasUncommittedChanges()) {
                    new RevertChangesDialog(new RevertChangesDialogPane(git, new SelectableFileViewer(status))).showAndWait();
                } else {
                    new Alert(Alert.AlertType.INFORMATION, "No changes have been registered since last commit", ButtonType.OK)
                            .showAndWait();
                }
            } catch (GitAPIException e) {
                e.printStackTrace();
            }
        });
    }

    /**
     * Constructs a RevertChangesMenuItem with the given text and no graphic
     */
    public RevertChangesMenuItem(String text, Val<Git> git) {
        this(text, null, git);
    }

    /**
     * Constructs a RevertChangesMenuItem with text "Revert changes..."
     */
    public RevertChangesMenuItem(Val<Git> git) {
        this("Revert changes...", git);
    }
}
