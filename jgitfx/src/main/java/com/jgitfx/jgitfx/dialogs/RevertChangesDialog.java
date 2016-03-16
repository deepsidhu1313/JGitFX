package com.jgitfx.jgitfx.dialogs;

import com.jgitfx.base.dialogs.RevertChangesDialogBase;
import com.jgitfx.jgitfx.fileviewers.SelectableFileViewer;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.Status;
import org.eclipse.jgit.lib.Ref;
import org.reactfx.value.Val;

/**
 * A basic implementation of {@link RevertChangesDialogBase}
 */
public class RevertChangesDialog extends RevertChangesDialogBase<RevertChangesResult, RevertChangesDialogPane> {

    public RevertChangesDialog(Val<Git> git, Status firstStatus) {
        super(git);
        setTitle("Revert modified files");
        setResizable(true);

        ButtonType revertButtonType = new ButtonType("Revert...", ButtonBar.ButtonData.YES);
        setDialogPane(new RevertChangesDialogPane(git, new SelectableFileViewer(firstStatus), revertButtonType));
        setResultConverter(buttonType -> {
           if (buttonType.equals(revertButtonType)) {
               return revertChanges();
           } else {
               return null;
           }
        });
    }

    @Override
    protected RevertChangesResult createResult(Ref ref) {
        return new RevertChangesResult(getDialogPane().getSelectedFiles());
    }
}
