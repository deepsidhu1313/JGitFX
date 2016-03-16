package com.jgitfx.base.dialogs;

import java.util.List;
import java.util.Optional;
import org.eclipse.jgit.api.AddCommand;
import org.eclipse.jgit.api.CommitCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.dircache.DirCache;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.treewalk.WorkingTreeIterator;
import org.reactfx.value.Val;

/**
 * A base CommitDialog class that handles most of the JGit code needed to add and then commit files.
 *
 * <p>Note: the result converter is not set in this class and leaves that up to subclasses. However,
 * the result converter should follow something along these lines:</p>
 * <pre>
 *     {@code
 *     CommitDialog dialog = // creation code;
 *     dialog.setResultConverter(buttonType -> {
 *         if (buttonType.equals(commitButtonType) {
 *             return dialog.addAndCommitSelectedFiles();
 *         } else {
 *             return null;
 *         }
 *     }
 *     }
 * </pre>
 *
 * <p>Though the JGit code implementation should suffice for most use cases, the JGit code can be customized via
 * {@link #setWorkingTreeIterator(WorkingTreeIterator)} for the {@link AddCommand} and
 * {@link #configureCommitCommand(CommitCommand)} for the {@link CommitCommand}</p>
 *
 * @param <R> the return result
 * @param <P> the pane class to use for the DialogPane
 */
public abstract class CommitDialogBase<R, P extends CommitDialogPaneBase> extends GitDialog<R, P> {

    private final Val<Git> git;
    protected final Git getGitOrThrow() {return git.getOrThrow(); }

    private Optional<WorkingTreeIterator> workingTreeIterator = Optional.empty();
    public final void setWorkingTreeIterator(WorkingTreeIterator iterator) { workingTreeIterator = Optional.of(iterator); }

    public CommitDialogBase(Val<Git> git) {
        super();
        this.git = git;
    }

    /**
     * Adds the files that were selected and commits them. Note: the {@link AddCommand}
     * and {@link CommitCommand} are used in this method. {@link AddCommand#setWorkingTreeIterator(WorkingTreeIterator)}
     * can be configured fia {@link #setWorkingTreeIterator(WorkingTreeIterator)} before calling this method,
     * and the {@code CommitCommand} can be configured via {@link #configureCommitCommand(CommitCommand)}.
     * @return the result of {@link #createResult(DirCache, RevCommit, List)} or null if
     *         a {@link GitAPIException} is thrown.
     */
    protected final R addAndCommitSelectedFiles() {
        List<String> selectedFiles = getDialogPane().getSelectedFiles();
        try {
            AddCommand add = getGitOrThrow().add();
            selectedFiles.forEach(add::addFilepattern);
            workingTreeIterator.ifPresent(add::setWorkingTreeIterator);
            DirCache cache = add.call();

            CommitCommand commit = getGitOrThrow().commit();
            configureCommitCommand(commit);
            RevCommit revCommit = commit.call();

            return createResult(cache, revCommit, selectedFiles);
        } catch (GitAPIException e) {
            handleGitAPIException(e);
            return null;
        }
    }

    /**
     * Method used to configure the {@link CommitCommand} before {@link CommitCommand#call()} is called.
     * Default configuration:
     * <pre>
     *     {@code
     *      P pane = getDialogPane();
     *      commitCmd
     *               .setAllowEmpty(false)  // insures newly tracked files are actually committed
     *               .setAmend(pane.isAmendCommit())
     *               .setMessage(pane.getCommitMessage())
     *               .setAuthor(pane.getAuthor());
     *     }
     * </pre>
     * @param commitCmd the commit command to configure
     */
    protected void configureCommitCommand(CommitCommand commitCmd) {
        P pane = getDialogPane();
        commitCmd
                .setAllowEmpty(false)
                .setAmend(pane.isAmendCommit())
                .setMessage(pane.getCommitMessage())
                .setAuthor(pane.getAuthor());
    }

    /**
     * Optional method for handling the returned results. Note: {@link #getDialogPane()} can still be used
     * to get other information not provided in the method arguments.
     * @param cache the result of the {@link AddCommand#call()}
     * @param commit the result of the {@link CommitCommand#call()}
     * @param selectedFiles the files that were committed
     */
    abstract protected R createResult(DirCache cache, RevCommit commit, List<String> selectedFiles);

    /**
     * If a {@link GitAPIException} is thrown, a developer can handle it here. Defaults to printing out stacktrace.
     * @param e the exception that might be thrown from {@link #addAndCommitSelectedFiles()}
     */
    protected void handleGitAPIException(GitAPIException e) {
        e.printStackTrace();
    }
}
