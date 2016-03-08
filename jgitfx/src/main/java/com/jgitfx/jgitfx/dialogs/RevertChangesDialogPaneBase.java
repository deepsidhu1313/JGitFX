package com.jgitfx.jgitfx.dialogs;

import javafx.beans.binding.Bindings;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DialogPane;
import org.eclipse.jgit.api.CheckoutCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.Status;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.reactfx.value.Val;

/**
 * Base class that handles JGit code for reverting (without making a new commit in the revert process)
 * tracked file(s) that were modified since the time of the most recent commit back
 * to the state they had in the most recent commit.
 *
 * @param <F> the object to use for displaying which files are selected
 */
public abstract class RevertChangesDialogPaneBase<F extends Node & FileSelecter> extends DialogPane {

    private final Val<Git> git;
    private Git getGitOrThrow() { return git.getOrThrow(); }

    private final F fileViewer;

    private final ButtonType revertButtonType;
    public final ButtonType getRevertButtonType() { return revertButtonType; }

    public RevertChangesDialogPaneBase(Val<Git> git, F fileSelector, ButtonType revertButtonType) {
        super();
        this.git = git;
        this.fileViewer = fileSelector;

        this.revertButtonType = revertButtonType;
        getButtonTypes().add(revertButtonType);

        Button revertButton = (Button) lookupButton(revertButtonType);
        revertButton.setOnAction(ae -> revertChanges());

        revertButton.disableProperty().bind(Bindings.not(fileViewer.hasSelectedFilesProperty()));
    }

    private void revertChanges() {
        CheckoutCommand checkout = getGitOrThrow().checkout();
        checkout.setStartPoint("HEAD");
        fileViewer.getSelectedFiles().forEach(checkout::addPath);
        try {
            checkout.call();
        } catch (GitAPIException e) {
            e.printStackTrace();
        }
    }

    /**
     * Refreshes the view to show any changes that might have affected the current files
     * shown by {@link #fileViewer}.
     *
     * <p>If modified tracked files have been manually reverted to their previous state or other unmodified
     * tracked files were modified, this method will call {@link #displayFileViewer(Status)} if
     * {@link Status#hasUncommittedChanges()} returns true and {@link #displayPlaceholder()} if it returns false.
     */
    protected final void refreshView() {
        try {
            Status status = getGitOrThrow().status().call();
            if (status.hasUncommittedChanges()) {
                displayFileViewer(status);
            } else {
                displayPlaceholder();
            }
        } catch (GitAPIException e) {
            e.printStackTrace();
        }
    }

    /**
     * Update the view to match the new {@link Status} of the Git repository
     */
    protected void displayFileViewer(Status status) {
        fileViewer.refreshTree(status);
    }

    /**
     * Update the view to inform the user that there are no changes registered.
     */
    protected abstract void displayPlaceholder();
}
