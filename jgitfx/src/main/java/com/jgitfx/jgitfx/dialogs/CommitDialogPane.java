package com.jgitfx.jgitfx.dialogs;

import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

import static com.jgitfx.jgitfx.dialogs.GitButtonTypes.COMMIT;
import static javafx.scene.control.ButtonType.CANCEL;

/**
 * The content of {@link CommitDialog}.
 */
public class CommitDialogPane extends DialogPane {

    /**
     * The {@link TextArea} used to type in the commit message
     */
    private final TextArea messageArea = new TextArea();
    protected final TextArea getMessageArea() { return messageArea; }

    /**
     * The {@link BorderPane} used to better layout the pane's content
     */
    private final BorderPane root = new BorderPane();
    protected final BorderPane getRoot() { return root; }

    CommitDialogPane() {
        super();
        setContent(root);
        root.setCenter(new VBox(
                new Label("Commit Message:"),
                messageArea
        ));

        getButtonTypes().addAll(COMMIT, CANCEL);
    }

    public final CommitModel getModel() {
        return null;
    }

}
