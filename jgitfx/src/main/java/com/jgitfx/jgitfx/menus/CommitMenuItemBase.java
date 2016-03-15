package com.jgitfx.jgitfx.menus;

import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.MenuItem;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.Status;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.reactfx.value.Val;

/**
 * A {@link MenuItem} with a method that will display a custom commit dialog or
 * inform the user that no changes have been made.
 *
 * <p>To add this functionality, use</p>
 * <pre>
 *     {@code
 *     CommitMenuItem cmItem = // creation code;
 *     cmItem.setOnAction(ae -> {
 *        // any necessary code if need be...
 *
 *        cmItem.commitOrInform();
 *
 *        // any necessary code if need be...
 *     });
 *     }
 * </pre>
 */
public abstract class CommitMenuItemBase extends MenuItem {

    private final Val<Git> git;

    public CommitMenuItemBase(Val<Git> git, String text, Node graphic) {
        super(text, graphic);
        this.git = git;
    }

    /**
     * If the repository has uncommitted changes on tracked files, calls {@link #displayCommitDialog(Val, Status)};
     * otherwise, calls {@link #displayNoChangesDialog()}.
     */
    public final void commitOrInform() {
        try {
            Status status = git.getOrThrow().status().call();
            if (status.hasUncommittedChanges()) {
                displayCommitDialog(git, status);
            } else {
                displayNoChangesDialog();
            }
        } catch (GitAPIException e) {
            handleGitAPIException(e);
        }
    }

    /**
     * Displays the commit dialog when there are tracked files with uncommitted changes.
     * @param git the high porcelain Git object used to refresh a {@link com.jgitfx.jgitfx.fileviewers.FileSelecter}.
     * @param firstStatus the Status used to construct the first {@link javafx.scene.control.TreeView} for displaying
     *                    the files with uncommitted changes.
     */
    protected abstract void displayCommitDialog(Val<Git> git, Status firstStatus);

    /**
     * Displays an information dialog informing user that there aren't any tracked files with uncommitted changes.
     */
    protected void displayNoChangesDialog() {
        new Alert(Alert.AlertType.INFORMATION, "No changes have been registered", ButtonType.OK)
                .showAndWait();
    }

    /**
     * If a {@link GitAPIException} is thrown, a developer can handle it here. Defaults to printing out stacktrace.
     * @param e the exception that might be thrown from {@link #commitOrInform()}
     */
    protected void handleGitAPIException(GitAPIException e) {
        e.printStackTrace();
    }
}
