package com.jgitfx.jgitfx.dialogs;

import com.jgitfx.jgitfx.fileviewers.SelectableFileViewer;
import javafx.beans.binding.Bindings;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
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
 */
public class RevertChangesDialogPaneBase extends DialogPane {

    private final Val<Git> git;
    private Git getGitOrThrow() { return git.getOrThrow(); }

    private final SelectableFileViewer fileViewer;
    protected final SelectableFileViewer getFileViewer() { return fileViewer; }

    private final ButtonType revertButtonType;
    public final ButtonType getRevertButtonType() { return revertButtonType; }

    public RevertChangesDialogPaneBase(Val<Git> git, Status status, ButtonBar.ButtonData revertButtonData) {
        super();
        this.git = git;
        this.fileViewer = new SelectableFileViewer(status);

        revertButtonType = new ButtonType("Revert", revertButtonData);
        getButtonTypes().add(revertButtonType);

        Button revertButton = (Button) lookupButton(revertButtonType);
        revertButton.setOnAction(ae -> revertChanges());

        revertButton.disableProperty().bind(Bindings.not(fileViewer.hasSelectedFilesProperty()));
    }

    private void revertChanges() {
        // Bug: does not account for an update that doesn't have any changes...
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
     * Refreshes the {@link SelectableFileViewer} if new files were added, tracked files removed,
     * or tracked files were modified recently.
     */
    protected final void refreshTree() {
        try {
            // Bug: does not account for an update where there are no changes....
            fileViewer.refreshTree(getGitOrThrow().status().call());
        } catch (GitAPIException e) {
            e.printStackTrace();
        }
    }
}
