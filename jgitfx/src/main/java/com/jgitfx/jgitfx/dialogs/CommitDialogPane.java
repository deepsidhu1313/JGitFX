package com.jgitfx.jgitfx.dialogs;

import com.jgitfx.jgitfx.fileviewers.SelectableFileTreeView;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import org.eclipse.jgit.api.Status;
import org.eclipse.jgit.lib.PersonIdent;

import static com.jgitfx.jgitfx.dialogs.GitButtonTypes.COMMIT;
import static javafx.scene.control.ButtonType.CANCEL;

/**
 * The content of {@link CommitDialog}.
 */
public class CommitDialogPane extends DialogPane {

    private final SelectableFileTreeView fileViewer;

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

    CommitDialogPane(Status status) {
        super();
        fileViewer = new SelectableFileTreeView(status);
        root.setTop(fileViewer);
        root.setCenter(new VBox(
                new Label("Commit Message:"),
                messageArea
        ));
        setContent(root);

        getButtonTypes().addAll(COMMIT, CANCEL);
        lookupButton(COMMIT).disableProperty().bind(fileViewer.hasSelectedFilesProperty().not());
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
