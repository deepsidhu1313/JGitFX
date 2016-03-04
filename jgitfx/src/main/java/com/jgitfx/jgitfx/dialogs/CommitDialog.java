package com.jgitfx.jgitfx.dialogs;

import javafx.scene.control.Dialog;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.Status;
import org.reactfx.value.Var;

import static com.jgitfx.jgitfx.dialogs.GitButtonTypes.COMMIT;

/**
 * A dialog for marking which files to add (stage) and what the commit message is before committing them.
 */
public class CommitDialog extends Dialog<CommitModel> {

    public CommitDialog(Var<Git> git, Status status) {
        super();
        setTitle("Commit changes");
        setResizable(true);

        CommitDialogPane pane = new CommitDialogPane(git, status);
        setDialogPane(pane);
        setResultConverter(buttonType ->
                buttonType.equals(COMMIT)
                        ? pane.getModel()
                        : null);
    }
}
