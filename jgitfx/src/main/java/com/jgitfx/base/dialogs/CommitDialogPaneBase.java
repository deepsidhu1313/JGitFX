package com.jgitfx.base.dialogs;

import java.util.List;
import javafx.scene.control.DialogPane;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.Status;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.PersonIdent;
import org.reactfx.value.Val;


public abstract class CommitDialogPaneBase extends DialogPane {

    private final Val<Git> git;
    protected final Git getGitOrThrow() { return git.getOrThrow(); }

    public CommitDialogPaneBase(Val<Git> git) {
        super();
        this.git = git;
    }

    public abstract List<String> getSelectedFiles();

    public abstract boolean isAmendCommit();

    public abstract PersonIdent getAuthor();

    public abstract PersonIdent getCommitter();

    public abstract String getCommitMessage();

    /**
     * Refreshes the view to show any changes that might have affected the current files.
     *
     * <p>If new files were added, tracked files removed, or tracked files were modified, this method
     * will call {@link #displayFileViewer(Status)} if {@link Status#hasUncommittedChanges()} returns
     * true and {@link #displayPlaceHolder()} if it returns false.
     */
    public final void refreshFileViewer() {
        try {
            Status status = getGitOrThrow().status().call();
            if (status.hasUncommittedChanges()) {
                displayFileViewer(status);
            } else {
                displayPlaceHolder();
            }
        } catch (GitAPIException e) {
            handleRefreshException(e);
        }
    };

    /**
     * Update the view to match the new {@link Status} of the Git repository
     */
    abstract protected void displayFileViewer(Status status);

    /**
     * Update the view to inform the user that there are no changes registered.
     */
    abstract protected void displayPlaceHolder();

    /**
     * Handles any {@link GitAPIException}s thrown from {@link #refreshFileViewer()}. Default implementation prints
     * the stracktrace.
     * @param e the exception that might be thrown from {@link #refreshFileViewer()}
     */
    protected void handleRefreshException(GitAPIException e) {
        e.printStackTrace();
    }



}
