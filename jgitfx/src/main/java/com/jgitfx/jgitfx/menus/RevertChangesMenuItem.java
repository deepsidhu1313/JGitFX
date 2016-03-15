package com.jgitfx.jgitfx.menus;

import com.jgitfx.base.menuItems.RevertChangesMenuItemBase;
import com.jgitfx.jgitfx.dialogs.RevertChangesDialog;
import com.jgitfx.jgitfx.dialogs.RevertChangesDialogPane;
import com.jgitfx.jgitfx.fileviewers.SelectableFileViewer;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.MenuItem;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.Status;
import org.reactfx.value.Val;


/**
 * A {@link MenuItem} that shows a {@link RevertChangesDialogPane} when
 * there are uncommitted changes and an alert dialog informing user there are no changes when
 * there are none.
 */
public class RevertChangesMenuItem extends RevertChangesMenuItemBase {

    public RevertChangesMenuItem(Val<Git> git, String text, Node graphic) {
        super(git, text, graphic);
    }

    /**
     * Constructs a RevertChangesMenuItem with the given text and no graphic
     */
    public RevertChangesMenuItem(Val<Git> git, String text) {
        this(git, text, null);
    }

    /**
     * Constructs a RevertChangesMenuItem with text "Revert changes..."
     */
    public RevertChangesMenuItem(Val<Git> git) {
        this(git, "Revert changes...");
    }

    @Override
    public void displayRevertDialog(Val<Git> git, Status firstStatus) {
        new RevertChangesDialog(new RevertChangesDialogPane(git, new SelectableFileViewer(firstStatus))).showAndWait();
    }

    @Override
    public void displayNoChangesDialog() {
        new Alert(Alert.AlertType.INFORMATION, "No changes have been registered since last commit", ButtonType.OK)
                .showAndWait();
    }
}
