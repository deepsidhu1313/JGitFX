package com.jgitfx.jgitfx.menus;

import com.jgitfx.base.menuItems.CommitMenuItemBase;
import com.jgitfx.jgitfx.dialogs.CommitDialog;
import com.jgitfx.jgitfx.dialogs.CommitDialogPane;
import com.jgitfx.jgitfx.fileviewers.SelectableFileViewer;
import javafx.scene.Node;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.Status;
import org.reactfx.value.Val;

/**
 * A {@link CommitMenuItemBase} that will display a {@link CommitDialog}.
 *
 */
public class CommitMenuItem extends CommitMenuItemBase {

    /**
     * Constructs a CommitMenuItem
     * @param text the MenuItem's text
     * @param graphic the MenuItem's graphic
     */
    public CommitMenuItem(Val<Git> git, String text, Node graphic) {
        super(git, text, graphic);
    }

    /**
     * Constructs a CommitMenuItem with the given text but no graphic.
     */
    public CommitMenuItem(Val<Git> git, String text) {
        this(git, text, null);
    }

    /**
     * Constructs a CommitMenuItem with the text "Commit..." and no graphic.
     */
    public CommitMenuItem(Val<Git> git) {
        this(git, "Commit...");
    }

    @Override
    protected void displayCommitDialog(Val<Git> git, Status firstStatus) {
        new CommitDialog(new CommitDialogPane(git, new SelectableFileViewer(firstStatus))).showAndWait();
    }

}
