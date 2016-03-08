package com.jgitfx.jgitfx.dialogs;

import javafx.scene.control.ButtonBar;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.Status;
import org.reactfx.value.Val;

/**
 * A basic implementation of {@link RevertChangesDialogPaneBase}.
 */
public class RevertChangesDialogPane extends RevertChangesDialogPaneBase {

    public RevertChangesDialogPane(Val<Git> git, Status status, ButtonBar.ButtonData revertButtonData) {
        super(git, status, revertButtonData);

        setContent(new VBox(
                new Label("Revert files back to previous commit"),
                getFileViewer()
        ));
    }
}
