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
import org.reactfx.value.Var;

import static com.jgitfx.jgitfx.dialogs.GitButtonTypes.COMMIT;
import static javafx.scene.control.ButtonType.CANCEL;

/**
 * The content of {@link CommitDialog}.
 */
public class CommitDialogPane extends DialogPane {

    private final Var<Git> git;

    private SelectableFileTreeView fileViewer;

    /**
     * The {@link TextArea} used to type in the commit message
     */
    private final TextArea messageArea = new TextArea();
    protected final TextArea getMessageArea() { return messageArea; }

    /**
     * The {@link BorderPane} used to better layout the pane's content
     */
    private final BorderPane root = new BorderPane();
    protected final BorderPane getBorderPane() { return root; }

    public CommitDialogPane(Var<Git> git, Status status) {
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

    private void refreshTree(Status status) {
        fileViewer = new SelectableFileTreeView(status);
        root.setCenter(fileViewer);

        Node commitButton = lookupButton(COMMIT);
        if (commitButton.disableProperty().isBound()) {
            commitButton.disableProperty().unbind();
        }
        commitButton.disableProperty().bind(fileViewer.hasSelectedFilesProperty().not());
    }

    public final void refreshTree() {
        try {
            refreshTree(git.getValue().status().call());
        } catch (GitAPIException e) {
            e.printStackTrace();
        }
    }

    public final CommitModel getModel() {
        return new CommitModel(fileViewer.getSelectedFiles(), getCommitMessage(), getAuthor(), getCommitter());
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
