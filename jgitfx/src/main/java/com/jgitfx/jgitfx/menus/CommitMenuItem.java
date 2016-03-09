package com.jgitfx.jgitfx.menus;

import com.jgitfx.jgitfx.dialogs.CommitDialog;
import com.jgitfx.jgitfx.dialogs.CommitDialogPane;
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
 * A {@link MenuItem} that displays a {@link CommitDialog} if there are changes or an information {@link Alert} that
 * notifies user that there are no changes to commit.
 */
public class CommitMenuItem extends MenuItem {

    /**
     * Constructs a CommitMenuItem
     * @param text the MenuItem's text
     * @param graphic the MenuItem's graphic
     */
    public CommitMenuItem(String text, Node graphic, Val<Git> git) {
        super(text, graphic);
        setOnAction(ae -> {
            try {
                Status status = git.getOrThrow().status().call();
                if (status.hasUncommittedChanges()) {
                    new CommitDialog(new CommitDialogPane(git, new SelectableFileViewer(status))).showAndWait();
                } else {
                    new Alert(Alert.AlertType.INFORMATION, "No changes have been registered", ButtonType.OK)
                        .showAndWait();
                }
            } catch (GitAPIException e) {
                e.printStackTrace();
            }
        });
    }

    /**
     * Constructs a CommitMenuItem with the given text but no graphic.
     */
    public CommitMenuItem(String text, Val<Git> git) {
        this(text, null, git);
    }

    /**
     * Constructs a CommitMenuItem with the text "Commit..." and no graphic.
     */
    public CommitMenuItem(Val<Git> git) {
        this("Commit...", git);
    }
}
