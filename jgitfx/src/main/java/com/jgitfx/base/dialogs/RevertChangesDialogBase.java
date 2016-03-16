package com.jgitfx.base.dialogs;

import java.util.List;
import org.eclipse.jgit.api.CheckoutCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Ref;
import org.reactfx.value.Val;

/**
 * A base class RevertChangesDialog class that handles most of the JGit code needed to revert tracked modified files
 * back to their previous states in the most recent commit.
 *
 * <p>Note: the result converter is not set in this class and leaves that up to subclasses. However,
 * the result converter should follow something along these lines:</p>
 * <pre>
 *     {@code
 *     RevertChangesDialog dialog = // creation code;
 *     dialog.setResultConverter(buttonType -> {
 *         if (buttonType.equals(revertButtonType) {
 *             return dialog.revertChanges();
 *         } else {
 *             return null;
 *         }
 *     }
 *     }
 * </pre>
 *
 * @param <R>
 * @param <P>
 */
public abstract class RevertChangesDialogBase<R, P extends RevertChangesDialogPaneBase> extends GitDialog<R, P> {

    private final Val<Git> git;
    protected final Git getGitOrThrow() {return git.getOrThrow(); }

    public RevertChangesDialogBase(Val<Git> git) {
        super();
        this.git = git;
    }

    /**
     * Reverts the selected files back to their previous state in the most recent commit
     * @return whatever {@link #createResult(Ref)} returns or null if a {@link GitAPIException} was thrown.
     */
    public final R revertChanges() {
        CheckoutCommand checkout = getGitOrThrow().checkout();
        List<String> selectedFiles = getDialogPane().getSelectedFiles();
        selectedFiles.forEach(checkout::addPath);
        try {
            Ref ref = checkout.call();
            return createResult(ref);
        } catch (GitAPIException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Optional method that creates the result using the result of the {@link CheckoutCommand}.
     * Note: {@link #getDialogPane()} can be used to get more arguments if need be.
     * @param ref the returned result of the {@link CheckoutCommand}.
     * @return the created result
     */
    protected abstract R createResult(Ref ref);

    /**
     * If a {@link GitAPIException} is thrown, a developer can handle it here. Defaults to printing out stacktrace.
     * @param e the exception that might be thrown from {@link #revertChanges()}
     */
    protected void handleGitAPIException(GitAPIException e) {
        e.printStackTrace();
    }

}
