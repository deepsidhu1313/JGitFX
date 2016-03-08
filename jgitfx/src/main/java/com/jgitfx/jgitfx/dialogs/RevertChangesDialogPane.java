package com.jgitfx.jgitfx.dialogs;

import com.jgitfx.jgitfx.fileviewers.SelectableFileViewer;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import org.eclipse.jgit.api.Git;
import org.reactfx.value.Val;

/**
 * A basic implementation of {@link RevertChangesDialogPaneBase}.
 */
public class RevertChangesDialogPane extends RevertChangesDialogPaneBase<SelectableFileViewer> {

    public RevertChangesDialogPane(Val<Git> git, SelectableFileViewer fileViewer) {
        super(git, fileViewer, new ButtonType("Revert", ButtonBar.ButtonData.YES));

        setContent(new VBox(
                new Label("Revert files back to previous commit"),
                fileViewer
        ));
    }
}
