package com.jgitfx.jgitfx.dialogs;

import javafx.scene.control.Dialog;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.Status;
import org.reactfx.value.Val;

/**
 * A dialog for marking which files to add (stage) and what the commit message is before committing them.
 */
public class CommitDialog extends Dialog<Void> {

    public CommitDialog(Val<Git> git, Status status) {
        super();
        setTitle("Commit changes");
        setResizable(true);

        setDialogPane(new CommitDialogPane(git, status));
    }
}
