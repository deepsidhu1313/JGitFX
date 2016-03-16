package com.jgitfx.base.dialogs;

import java.util.List;
import javafx.scene.control.DialogPane;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.Status;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.reactfx.value.Val;

/**
 * A base RevertChangesDialog class that is used alongside of {@link RevertChangesDialogBase} to handle most of the
 * JGit code needed to revert a modified tracked file back to its state in the most recent commit.
 *
 * <p>Note: a subclasses will need to add the revert button via {@link DialogPane#getButtonTypes()}.
 * Additionally, they will want to bind the button's disable property to the fileViewer:</p>
 * <pre>
 *     {@code
 *     public SubClassRevertDialogPane(args...) {
 *         // code....
 *         getButtonTypes().add(revertButtonType);
 *
 *         Button revertButton = (Button) lookupButton(revertButtonType);
 *         revertButton.disableProperty().bind(Bindings.not(fileViewer.hasSelectedFilesProperty()));
 *     }
 *     }
 * </pre>
 */
public abstract class RevertChangesDialogPaneBase extends DialogPane {

    private final Val<Git> git;
    protected final Git getGitOrThrow() { return git.getOrThrow(); }

    public RevertChangesDialogPaneBase(Val<Git> git) {
        super();
        this.git = git;
    }

    /**
     * @return the files that were selected before the revert button was clicked.
     */
    public abstract List<String> getSelectedFiles();

    /**
     * Refreshes the view to show any changes that might have affected the current files.
     *
     * <p>If modified tracked files have been manually reverted to their previous state or other unmodified
     * tracked files were modified, this method will call {@link #displayFileViewer(Status)} if
     * {@link Status#hasUncommittedChanges()} returns true and {@link #displayPlaceholder()} if it returns false.
     */
    public final void refreshFileViewer() {
        try {
            Status status = getGitOrThrow().status().call();
            if (status.hasUncommittedChanges()) {
                displayFileViewer(status);
            } else {
                displayPlaceholder();
            }
        } catch (GitAPIException e) {
            handleRefreshException(e);
        }
    }

    /**
     * Update the view to match the new {@link Status} of the Git repository
     */
    protected abstract void displayFileViewer(Status status);

    /**
     * Update the view to inform the user that there are no changes registered.
     */
    protected abstract void displayPlaceholder();

    /**
     * If a {@link GitAPIException} is thrown, a developer can handle it here. Defaults to printing out stacktrace.
     * @param e the exception that might be thrown from {@link #refreshFileViewer()}
     */
    protected void handleRefreshException(GitAPIException e) {
        e.printStackTrace();
    }
}
