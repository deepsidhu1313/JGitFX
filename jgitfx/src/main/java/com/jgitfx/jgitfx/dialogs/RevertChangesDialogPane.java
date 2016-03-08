package com.jgitfx.jgitfx.dialogs;

import com.jgitfx.jgitfx.fileviewers.SelectableFileViewer;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.Status;
import org.reactfx.value.Val;

/**
 * A basic implementation of {@link RevertChangesDialogPaneBase}.
 */
public class RevertChangesDialogPane extends RevertChangesDialogPaneBase<SelectableFileViewer> {

    private final VBox vbox = new VBox();

    public RevertChangesDialogPane(Val<Git> git, SelectableFileViewer fileViewer) {
        super(git, fileViewer, new ButtonType("Revert", ButtonBar.ButtonData.YES));

        vbox.getChildren().addAll(
                new Label("Revert selected files back to previous commit"),
                fileViewer
        );
        setContent(vbox);
    }

    @Override
    protected void displayFileViewer(Status status) {
        if (!vbox.getChildren().contains(getFileViewer())) {
            vbox.getChildren().set(1, getFileViewer());
        }
        getFileViewer().refreshTree(status);
    }

    @Override
    protected void displayPlaceholder() {
        if (vbox.getChildren().contains(getFileViewer())) {
            vbox.getChildren().set(1, new StackPane(new Label("No changes detected...")));
        }
    }
}
