package com.jgitfx.jgitfx.dialogs;

import com.jgitfx.base.dialogs.RevertChangesDialogPaneBase;
import com.jgitfx.jgitfx.fileviewers.SelectableFileViewer;
import java.util.List;
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
public class RevertChangesDialogPane extends RevertChangesDialogPaneBase {

    // GUI components
    private final SelectableFileViewer fileViewer;
    public List<String> getSelectedFiles() {return fileViewer.getSelectedFiles(); }

    // layout containers
    private final VBox vbox = new VBox();

    public RevertChangesDialogPane(Val<Git> git, SelectableFileViewer fileViewer, ButtonType revertButtonType) {
        super(git);
        this.fileViewer = fileViewer;

        vbox.getChildren().addAll(
                new Label("Revert selected files back to previous commit"),
                fileViewer
        );
        setContent(vbox);
    }

    protected void displayFileViewer(Status status) {
        if (!vbox.getChildren().contains(fileViewer)) {
            vbox.getChildren().set(1, fileViewer);
        }
        fileViewer.refreshTree(status);
    }

    protected void displayPlaceholder() {
        if (vbox.getChildren().contains(fileViewer)) {
            vbox.getChildren().set(1, new StackPane(new Label("No changes detected...")));
        }
    }
}
