package com.jgitfx.jgitfx.dialogs;

import javafx.scene.control.Dialog;

import java.util.Optional;

/**
 * A dialog for displaying subclasses of {@link RevertChangesDialogPaneBase}
 */
public class RevertChangesDialog extends Dialog<Optional<RevertChangesResult>> {

    public RevertChangesDialog(RevertChangesDialogPaneBase pane) {
        super();
        setTitle("Revert local changes");
        setResizable(true);

        setDialogPane(pane);
        setResultConverter(buttonType -> Optional.ofNullable(pane.getRevertChangesResult()));
    }
}
