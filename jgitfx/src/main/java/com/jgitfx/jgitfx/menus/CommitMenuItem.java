package com.jgitfx.jgitfx.menus;

import com.jgitfx.jgitfx.dialogs.CommitDialog;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.MenuItem;
import org.eclipse.jgit.api.AddCommand;
import org.eclipse.jgit.api.CommitCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.Status;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.revwalk.RevCommit;
import org.reactfx.value.Val;

import java.util.function.Consumer;

/**
 * A {@link MenuItem} that displays a {@link CommitDialog} if there are changes or an information {@link Alert} that
 * notifies user that there are no changes to commit.
 */
public class CommitMenuItem extends MenuItem {

    private final Val<Git> git;
    public final Git getGit() { return git.getValue(); }

    Consumer<RevCommit> revCommitConsumer;

    public CommitMenuItem(Val<Git> git, Consumer<RevCommit> commitConsumer) {
        super("Commit...");
        this.git = git;
        revCommitConsumer = commitConsumer;
        setOnAction(ae -> {
            try {
                Status status = getGit().status().call();
                if (status.hasUncommittedChanges()) {
                    CommitDialog dialog = new CommitDialog(status);
                    dialog.showAndWait().ifPresent(commitModel -> {
                        try {
                            // stage files that were selected
                            AddCommand add = getGit().add();
                            commitModel.getCommittedFiles().forEach(add::addFilepattern);
                            add.call();

                            // commit files
                            CommitCommand commit = getGit().commit();
                            commitModel.ifAuthorIsPresent(commit::setAuthor);
                            commitModel.ifCommitterIsPresent(commit::setCommitter);
                            commitModel.ifCommitMessageIsPresent(commit::setMessage);
                            RevCommit revCommit = commit.call();

                            revCommitConsumer.accept(revCommit);
                        } catch (GitAPIException e) {
                            e.printStackTrace();
                        }
                    });
                } else {
                    new Alert(Alert.AlertType.INFORMATION, "No changes have been registered", ButtonType.OK)
                        .showAndWait();
                }
            } catch (GitAPIException e) {
                e.printStackTrace();
            }
        });
    }
}
