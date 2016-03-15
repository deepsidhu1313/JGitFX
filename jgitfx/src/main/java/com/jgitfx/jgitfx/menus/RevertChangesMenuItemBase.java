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
 * A {@link MenuItem} with a method that shows a dialog for reverting changes when
 * there are uncommitted changes and an alert dialog informing user there are no changes when
 * there are none.
 *
 * <p>To add the functionality, use</p>
 * <pre>
 *     {@code
 *     RevertChangesMenuItem rcmItem = // creation code;
 *     rcmItem.setOnAction(ae -> {
 *        // any necessary code
 *
 *        rcmItem.revertOrInform();
 *
 *        // any other code (if needed)...
 *     });
 *     }
 * </pre>
 */
public abstract class RevertChangesMenuItemBase extends MenuItem {

    private final Val<Git> git;

    public RevertChangesMenuItemBase(Val<Git> git, String text, Node graphic) {
        super(text, graphic);
        this.git = git;
    }

    /**
     * When there are changes in tracked files that can be reverted, calls {@link #displayRevertDialog(Val, Status)}.
     * Otherwise, calls {@link #displayNoChangesDialog()}.
     */
    public final void revertOrInform() {
        try {
            Status status = git.getOrThrow().status().call();
            if (status.hasUncommittedChanges()) {
                displayRevertDialog(git, status);
            } else {
                displayNoChangesDialog();
            }
        } catch (GitAPIException e) {
            handleGitAPIException(e);
        }
    }

    /**
     * Displays the revert dialog
     * @param git the high porcelain Git object used to refresh a {@link com.jgitfx.jgitfx.fileviewers.FileSelecter}.
     * @param firstStatus the Status used to construct the first {@link javafx.scene.control.TreeView} for displaying
     *                    the files with uncommitted changes.
     */
    public abstract void displayRevertDialog(Val<Git> git, Status firstStatus);

    /**
     * Displays an information dialog informing user that there aren't any tracked files with uncommitted changes.
     */
    public void displayNoChangesDialog() {
        new Alert(Alert.AlertType.INFORMATION, "No changes have been registered since last commit", ButtonType.OK)
                .showAndWait();
    }

    /**
     * If a {@link GitAPIException} is thrown, a developer can handle it here. Defaults to printing out stacktrace.
     * @param e the exception that might be thrown from {@link #revertOrInform()}
     */
    protected void handleGitAPIException(GitAPIException e) {
        e.printStackTrace();
    }
}
