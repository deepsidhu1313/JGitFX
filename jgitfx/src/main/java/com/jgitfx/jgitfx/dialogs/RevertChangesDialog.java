package com.jgitfx.jgitfx.dialogs;

import javafx.scene.control.Dialog;

/**
 * A dialog for displaying subclasses of {@link RevertChangesDialogPaneBase}
 */
public class RevertChangesDialog extends Dialog<Void> {

    public RevertChangesDialog(RevertChangesDialogPaneBase pane) {
        super();
        setTitle("Revert local changes");
        setResizable(true);

        setDialogPane(pane);
    }
}
