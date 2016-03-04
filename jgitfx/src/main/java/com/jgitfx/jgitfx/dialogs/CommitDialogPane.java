package com.jgitfx.jgitfx.dialogs;

import com.jgitfx.jgitfx.fileviewers.SelectableFileTreeView;
import javafx.scene.Node;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.Status;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.PersonIdent;
import org.reactfx.value.Val;

import static com.jgitfx.jgitfx.dialogs.GitButtonTypes.COMMIT;
import static javafx.scene.control.ButtonType.CANCEL;

/**
 * The content of {@link CommitDialog}.
 */
public final class CommitDialogPane extends DialogPane {

    private final Val<Git> git;
    private final TextArea messageArea = new TextArea();
    private final BorderPane root = new BorderPane();

    private SelectableFileTreeView fileViewer;

    public CommitDialogPane(Val<Git> git, Status status) {
        super();
        this.git = git;

        setContent(root);
        root.setBottom(new VBox(
                new Label("Commit Message:"),
                messageArea
        ));

        getButtonTypes().addAll(COMMIT, CANCEL);
        refreshTree(status);
    }

    public final void refreshTree() {
        try {
            refreshTree(git.getOrThrow().status().call());
        } catch (GitAPIException e) {
            e.printStackTrace();
        }
    }

    public final CommitModel getModel() {
        return new CommitModel(fileViewer.getSelectedFiles(), getCommitMessage(), getAuthor(), getCommitter());
    }

    private void refreshTree(Status status) {
        fileViewer = new SelectableFileTreeView(status);
        root.setCenter(fileViewer);

        Node commitButton = lookupButton(COMMIT);
        if (commitButton.disableProperty().isBound()) {
            commitButton.disableProperty().unbind();
        }
        commitButton.disableProperty().bind(fileViewer.hasSelectedFilesProperty().not());
    }

    public final String getCommitMessage() {
        return messageArea.getText();
    }

    public final PersonIdent getAuthor() {
        return null;
    }

    public final PersonIdent getCommitter() {
        return null;
    }
}
