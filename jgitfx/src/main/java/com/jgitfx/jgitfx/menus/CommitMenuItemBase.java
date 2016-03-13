package com.jgitfx.jgitfx.menus;

import javafx.scene.Node;
import javafx.scene.control.MenuItem;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.Status;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.reactfx.value.Val;

public abstract class CommitMenuItemBase extends MenuItem {
    
    public CommitMenuItemBase(Val<Git> git, String text, Node graphic) {
        super(text, graphic);
        setOnAction(ae -> {
            try {
                Status status = git.getOrThrow().status().call();
                if (status.hasUncommittedChanges()) {
                    displayCommitDialog(git, status);
                } else {
                    displayNoChangesDialog();
                }
            } catch (GitAPIException e) {
                e.printStackTrace();
            }
        });
    }

    public CommitMenuItemBase(Val<Git> git, String text) {
        this(git, text, null);
    }
    
    protected abstract void displayCommitDialog(Val<Git> git, Status status);

    protected abstract void displayNoChangesDialog();
}
