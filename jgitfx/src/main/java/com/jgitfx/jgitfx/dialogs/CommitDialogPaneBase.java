package com.jgitfx.jgitfx.dialogs;

import javafx.beans.binding.Bindings;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DialogPane;
import org.eclipse.jgit.api.AddCommand;
import org.eclipse.jgit.api.CommitCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.Status;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.PersonIdent;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.treewalk.WorkingTreeIterator;
import org.reactfx.value.Val;

import java.util.List;
import java.util.Optional;

/**
 * A base class for designing {@code CommitDialogPane}s.
 *
 * <p>All the code for committing the selected files is handled
 * in this class, but subclasses can customize the layout. When the user
 * clicks on the {@code Commit Button}, the selected files are added (staged)
 * before being committed. The defaults should suffice for most users needs,
 * but a developer can configure the {@link AddCommand}'s {@link WorkingTreeIterator}
 * via {@link #setWorkingTreeIterator(WorkingTreeIterator)} and the {@link CommitCommand}
 * via {@link #configureCommitCommand(CommitCommand)}</p>
 *
 * @param <F> the object to use for displaying which files are selected
 */
public abstract class CommitDialogPaneBase<F extends Node & FileSelecter> extends DialogPane {

    private final Val<Git> git;
    private Git getGitOrThrow() { return git.getOrThrow(); }

    private final F fileViewer;
    protected final F getFileViewer() { return fileViewer; }

    private Optional<WorkingTreeIterator> workingTreeIterator = Optional.empty();
    protected final void setWorkingTreeIterator(WorkingTreeIterator iterator) { workingTreeIterator = Optional.of(iterator); }

    protected abstract String getCommitMessage();

    protected abstract boolean isAmendCommit();

    protected abstract PersonIdent getAuthor();

    private final ButtonType commitButtonType;
    public final ButtonType getCommitButtonType() { return commitButtonType; }

    private CommitResult result;
    public final CommitResult getResult() { return result; }

    /**
     * Base constructor for a CommitDialogPane
     *
     * @param commitButtonType the type to use for the commit button
     */
    public CommitDialogPaneBase(Val<Git> git, F fileSelector, ButtonType commitButtonType) {
        super();
        this.git = git;
        this.fileViewer = fileSelector;

        this.commitButtonType = commitButtonType;
        getButtonTypes().add(commitButtonType);

        Button commitButton = (Button) lookupButton(commitButtonType);
        commitButton.setOnAction(ae -> addAndCommitSelectedFiles());

        // commit button is disabled when there are no selected files
        commitButton.disableProperty().bind(Bindings.not(fileViewer.hasSelectedFilesProperty()));
    }

    private void addAndCommitSelectedFiles() {
        List<String> selectedFiles = fileViewer.getSelectedFiles();
        try {
            AddCommand add = getGitOrThrow().add();
            selectedFiles.forEach(add::addFilepattern);
            workingTreeIterator.ifPresent(add::setWorkingTreeIterator);
            add.call();

            CommitCommand commit = getGitOrThrow().commit();
            configureCommitCommand(commit);
            RevCommit revCommit = commit.call();

            result = new CommitResult(selectedFiles, revCommit);
        } catch (GitAPIException e) {
            e.printStackTrace();
        }
    }

    /**
     * Method used to configure the {@link CommitCommand} before it is called. Default
     * configuration:
     * <pre>
     *     {@code
     *      commitCmd.setAllowEmpty(false)
     *               .setAmend(isAmendCommit())
     *               .setMessage(getCommitMessage())
     *               .setAuthor(getAuthor());
     *     }
     * </pre>
     * @param commitCmd the commit command to configure
     */
    protected void configureCommitCommand(CommitCommand commitCmd) {
        commitCmd.setAllowEmpty(false)
                .setAmend(isAmendCommit())
                .setMessage(getCommitMessage())
                .setAuthor(getAuthor());
    }

    /**
     * Refreshes the view to show any changes that might have affected the current files
     * shown by {@link #fileViewer}.
     *
     * <p>If new files were added, tracked files removed, or tracked files were modified, this method
     * will call {@link #displayFileViewer(Status)} if {@link Status#hasUncommittedChanges()} returns
     * true and {@link #displayPlaceHolder()} if it returns false.
     */
    protected final void refreshView() {
        try {
            Status status = getGitOrThrow().status().call();
            if (status.hasUncommittedChanges()) {
                displayFileViewer(status);
            } else {
                displayPlaceHolder();
            }
        } catch (GitAPIException e) {
            e.printStackTrace();
        }
    }

    /**
     * Update the view to match the new {@link Status} of the Git repository
     */
    protected void displayFileViewer(Status status) {
        fileViewer.refreshTree(status);
    }

    /**
     * Update the view to inform the user that there are no changes registered.
     */
    protected abstract void displayPlaceHolder();

}
