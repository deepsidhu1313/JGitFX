package com.jgitfx.jgitfx.dialogs;

import javafx.scene.control.Dialog;

/**
 * A dialog for marking which files to add (stage) and what the commit message is before committing them.
 */
public class CommitDialog extends Dialog<CommitResult> {

    public CommitDialog(CommitDialogPaneBase commitDialogPane) {
        super();
        setTitle("Commit changes");
        setResizable(true);

        setDialogPane(commitDialogPane);
        setResultConverter(buttonType ->
                buttonType.equals(commitDialogPane.getCommitButtonType())
                        ? commitDialogPane.getCommitResult()
                        : null
        );
    }
}
