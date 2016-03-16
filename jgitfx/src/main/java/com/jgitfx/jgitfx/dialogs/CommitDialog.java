package com.jgitfx.jgitfx.dialogs;

import com.jgitfx.base.dialogs.CommitDialogBase;
import com.jgitfx.jgitfx.fileviewers.SelectableFileViewer;
import java.util.List;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.Status;
import org.eclipse.jgit.dircache.DirCache;
import org.eclipse.jgit.revwalk.RevCommit;
import org.reactfx.value.Val;

/**
 * A dialog for marking which files to add (stage) and what the commit message is before committing them.
 */
public class CommitDialog extends CommitDialogBase<CommitResult, CommitDialogPane> {

    public CommitDialog(Val<Git> git, Status firstStatus) {
        super(git);
        setTitle("Commit changes");
        setResizable(true);

        ButtonType commitButton = new ButtonType("Commit...", ButtonBar.ButtonData.YES);
        setDialogPane(new CommitDialogPane(git, new SelectableFileViewer(firstStatus), commitButton));
        setResultConverter(buttonType -> {
            if (buttonType.equals(commitButton)) {
                return addAndCommitSelectedFiles();
            } else {
                return null;
            }
        });
    }

    @Override
    protected CommitResult createResult(DirCache cache, RevCommit commit, List<String> selectedFiles) {
        return new CommitResult(selectedFiles, commit);
    }
}
