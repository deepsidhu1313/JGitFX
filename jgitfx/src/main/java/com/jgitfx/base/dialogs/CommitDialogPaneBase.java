package com.jgitfx.base.dialogs;

import java.util.List;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DialogPane;
import org.eclipse.jgit.lib.PersonIdent;


public abstract class CommitDialogPaneBase extends DialogPane {

    public CommitDialogPaneBase() {
        super();
    }

    public abstract ButtonType getCommitButton();

    public abstract List<String> getSelectedFiles();

    public abstract boolean isAmendCommit();

    public abstract PersonIdent getAuthor();

    public abstract PersonIdent getCommitter();

    public abstract String getCommitMessage();
}
