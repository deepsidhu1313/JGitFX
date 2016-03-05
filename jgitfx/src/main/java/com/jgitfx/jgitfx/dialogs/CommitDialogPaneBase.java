package com.jgitfx.jgitfx.dialogs;

import com.jgitfx.jgitfx.fileviewers.SelectableFileViewer;
import javafx.beans.binding.Bindings;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DialogPane;
import org.eclipse.jgit.api.AddCommand;
import org.eclipse.jgit.api.CommitCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.Status;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.dircache.DirCache;
import org.eclipse.jgit.revwalk.RevCommit;
import org.reactfx.value.Val;

/**
 * A base class for designing {@code CommitDialogPane}s.
 * <p>All the code for committing the selected files is handled
 * in this class, but subclasses can customize the layout. When the user
 * clicks on the {@code Commit Button}, {@link #addAndCommitSelectedFiles()}
 * is called, which calls {@link #addFiles()}, then {@link #commitFiles()}.
 * Each of these </p>
 *
 * <p>Additionally, if one wants to modify the add call and commit call,
 * they can override {@link #addFiles()} and {@link #commitFiles()}.
 */
public abstract class CommitDialogPaneBase extends DialogPane {

    private final Val<Git> git;
    protected final Git getGitOrThrow() { return git.getOrThrow(); }

    private final SelectableFileViewer fileViewer;
    protected final SelectableFileViewer getFileViewer() { return fileViewer; }

    protected abstract String getCommitMessage();

    protected abstract boolean isAmendCommit();

    /**
     * Base constructor for a CommitDialogPane
     *
     * @param git the Git repository
     * @param status the initial status to use to initialize {@link SelectableFileViewer}
     * @param commitButtonData determines where the {@code Commit Button} will appear in
     *                         the ButtonBar if other Buttons are added.
     */
    public CommitDialogPaneBase(Val<Git> git, Status status, ButtonBar.ButtonData commitButtonData) {
        this.git = git;
        this.fileViewer = new SelectableFileViewer(status);

        ButtonType commitButtonType = new ButtonType("Commit", commitButtonData);
        getButtonTypes().add(commitButtonType);

        Button commitButton = (Button) lookupButton(commitButtonType);
        commitButton.setOnAction(ae -> addAndCommitSelectedFiles());

        // commit button is disabled when there are no selected files
        commitButton.disableProperty().bind(Bindings.not(fileViewer.hasSelectedFilesProperty()));
    }

    /**
     * When a user clicks on the {@code Commit} button, this method will be called.
     * It calls {@link #addFiles()} followed by {@link #commitFiles()}.
     *
     * @return the RevCommit returned from {@link CommitCommand#call()} or null if an exception occurs
     */
    protected final RevCommit addAndCommitSelectedFiles() {
        addFiles();
        return commitFiles();
    }

    /**
     * Adds (stages) the selected files from {@link SelectableFileViewer#getSelectedFiles()}
     *
     * <p>Note: this method can be overridden in subclasses for customized options, but that
     * shouldn't be necessary.</p>
     *
     * @return the DirCache that is returned from {@link AddCommand#call()} or null if an exception occurs
     * @see {@link #addAndCommitSelectedFiles()}
     */
    protected DirCache addFiles() {
        try {
            AddCommand add = getGitOrThrow().add();
            fileViewer.getSelectedFiles().forEach(add::addFilepattern);
            return add.call();
        } catch (GitAPIException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Commits the selected files that were staged in {@link #addFiles()}.
     *
     * <p>Note: this method can be overridden in subclasses for customized options,
     * but that shouldn't be necessary for most use-cases.</p>
     *
     * @return the RevCommit returned from {@link CommitCommand#call()}
     * @see {@link #addAndCommitSelectedFiles()} or null if an exception occurs
     */
    protected RevCommit commitFiles() {
        try {
            return getGitOrThrow().commit()
                    .setAllowEmpty(false)
                    .setAmend(isAmendCommit())
                    .setMessage(getCommitMessage())
                    // .setAuthor(); // TODO implement author
                    .call();
        } catch (GitAPIException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Refreshes the {@link SelectableFileViewer} if new files were added, tracked files removed,
     * or tracked files were modified recently.
     */
    protected final void refreshTree() {
        try {
            fileViewer.refreshTree(getGitOrThrow().status().call());
        } catch (GitAPIException e) {
            e.printStackTrace();
        }
    }

}
