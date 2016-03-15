package com.jgitfx.jgitfx.dialogs;

import javafx.scene.control.Dialog;

import java.util.Optional;

/**
 * A dialog for marking which files to add (stage) and what the commit message is before committing them.
 */
public class CommitDialog extends Dialog<Optional<CommitResult>> {

    public CommitDialog(CommitDialogPaneBaseOld pane) {
        super();
        setTitle("Commit changes");
        setResizable(true);

        setDialogPane(pane);
        setResultConverter(buttonType -> Optional.ofNullable(pane.getResult()));
    }
}
