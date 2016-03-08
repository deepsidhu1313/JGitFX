package com.jgitfx.jgitfx.menus;

import com.jgitfx.jgitfx.dialogs.RevertChangesDialog;
import com.jgitfx.jgitfx.dialogs.RevertChangesDialogPane;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
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

    public RevertChangesMenuItem(Val<Git> git) {
        super("Revert local changes...");
        setOnAction(ae -> {
            try {
                Status status = git.getOrThrow().status().call();
                if (status.hasUncommittedChanges()) {
                    new RevertChangesDialog(new RevertChangesDialogPane(git, status, ButtonBar.ButtonData.APPLY)).showAndWait();
                } else {
                    new Alert(Alert.AlertType.INFORMATION, "No changes have been registered since last commit", ButtonType.OK)
                            .showAndWait();
                }
            } catch (GitAPIException e) {
                e.printStackTrace();
            }
        });
    }
}
