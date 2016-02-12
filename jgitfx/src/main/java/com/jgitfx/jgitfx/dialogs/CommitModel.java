package com.jgitfx.jgitfx.dialogs;

import org.eclipse.jgit.annotations.Nullable;
import org.eclipse.jgit.lib.PersonIdent;

import java.util.List;
import java.util.function.Consumer;

/**
 * The result of pressing {@link CommitDialog}'s {@code Commit} button that is used for committing changes.
 */
public class CommitModel {

    private final PersonIdent author;
    public final void ifAuthorIsPresent(Consumer<PersonIdent> authorConsumer) {
        if (author != null) { authorConsumer.accept(author); }
    }

    private final PersonIdent committer;
    public final void ifCommitterIsPresent(Consumer<PersonIdent> committerConsumer) {
        if (committer != null) { committerConsumer.accept(committer); }
    }

    private final String commitMessage;
    public final void ifCommitMessageIsPresent(Consumer<String> messageConsumer) {
        if (commitMessage != null) { messageConsumer.accept(commitMessage); }
    }

    private List<String> committedFiles;
    public final List<String> getCommittedFiles() { return committedFiles; }

    CommitModel(List<String> files, @Nullable String message, @Nullable PersonIdent author, @Nullable PersonIdent committer) {
        committedFiles = files;
        this.commitMessage = message;
        this.author = author;
        this.committer = committer;
    }


}
