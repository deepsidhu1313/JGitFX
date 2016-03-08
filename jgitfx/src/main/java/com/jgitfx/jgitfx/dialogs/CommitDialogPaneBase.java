package com.jgitfx.jgitfx.dialogs;

import com.jgitfx.jgitfx.fileviewers.SelectableFileViewer;
import javafx.beans.binding.Bindings;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DialogPane;
import org.eclipse.jgit.api.AddCommand;
import org.eclipse.jgit.api.CommitCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.PersonIdent;
import org.eclipse.jgit.revwalk.RevCommit;
import org.reactfx.value.Val;

/**
 * A base class for designing {@code CommitDialogPane}s.
 *
 * <p>All the code for committing the selected files is handled
 * in this class, but subclasses can customize the layout. When the user
 * clicks on the {@code Commit Button}, the selected files are added (staged)
 * before being committed. The defaults should suffice for most users needs,
 * but a developer can configure the {@link AddCommand} and {@link CommitCommand}
 * via {@link #configureAddCommand(AddCommand)} and
 * {@link #configureCommitCommand(CommitCommand)}, respectively.</p>
 *
 * @param <F> the object to use for displaying which files are selected
 */
public abstract class CommitDialogPaneBase<F extends Node & FileSelecter> extends DialogPane {

    private final Val<Git> git;
    private Git getGitOrThrow() { return git.getOrThrow(); }

    private final F fileViewer;

    protected abstract String getCommitMessage();

    protected abstract boolean isAmendCommit();

    protected abstract PersonIdent getAuthor();

    private final ButtonType commitButtonType;
    public final ButtonType getCommitButtonType() { return commitButtonType; }

    private CommitResult result;

    /**
     * Base constructor for a CommitDialogPane
     *
     * @param commitButtonType the type to use for the commit button
     */
    public CommitDialogPaneBase(Val<Git> git, F fileSelector, ButtonType commitButtonType) {
        this.git = git;
        this.fileViewer = fileSelector;

        this.commitButtonType = commitButtonType;
        getButtonTypes().add(commitButtonType);

        Button commitButton = (Button) lookupButton(commitButtonType);
        commitButton.setOnAction(ae -> addAndCommitSelectedFiles());

        // commit button is disabled when there are no selected files
        commitButton.disableProperty().bind(Bindings.not(fileViewer.hasSelectedFilesProperty()));
    }

    /**
     * Note: the {@link CommitResult}'s list of affected files assumes that the files added all
     * came from {@link SelectableFileViewer#getSelectedFiles()}. If a developer deviates from this,
     * ths affected files will not be correct.
     *
     * @return the result of the commit
     */
    public final CommitResult getCommitResult() {
        return result;
    }

    private void addAndCommitSelectedFiles() {
        try {
            AddCommand add = getGitOrThrow().add();
            configureAddCommand(add);
            add.call();

            CommitCommand commit = getGitOrThrow().commit();
            configureCommitCommand(commit);
            RevCommit revCommit = commit.call();

            result = new CommitResult(fileViewer.getSelectedFiles(), revCommit);
        } catch (GitAPIException e) {
            e.printStackTrace();
        }
    }

    /**
     * Method used to configure the {@link AddCommand} before it is called. Default
     * configuration adds the selected files.
     * @param addCmd the add command to configure
     */
    protected void configureAddCommand(AddCommand addCmd) {
        fileViewer.getSelectedFiles().forEach(addCmd::addFilepattern);
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
     * Refreshes the {@link SelectableFileViewer} if new files were added, tracked files removed,
     * or tracked files were modified recently.
     */
    protected final void refreshTree() {
        try {
            // Bug: if changes are reverted, then file tree should display a note saying that
            fileViewer.refreshTree(getGitOrThrow().status().call());
        } catch (GitAPIException e) {
            e.printStackTrace();
        }
    }

}
