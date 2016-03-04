package com.jgitfx.jgitfx.menus;

import com.jgitfx.jgitfx.dialogs.CommitDialog;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.MenuItem;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.Status;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.reactfx.value.Val;

/**
 * A {@link MenuItem} that displays a {@link CommitDialog} if there are changes or an information {@link Alert} that
 * notifies user that there are no changes to commit.
 */
public class CommitMenuItem extends MenuItem {

    public CommitMenuItem(Val<Git> git) {
        super("Commit...");
        setOnAction(ae -> {
            try {
                Status status = git.getOrThrow().status().call();
                if (status.hasUncommittedChanges()) {
                    new CommitDialog(git, status).showAndWait();
                } else {
                    new Alert(Alert.AlertType.INFORMATION, "No changes have been registered", ButtonType.OK)
                        .showAndWait();
                }
            } catch (GitAPIException e) {
                e.printStackTrace();
            }
        });
    }
}
