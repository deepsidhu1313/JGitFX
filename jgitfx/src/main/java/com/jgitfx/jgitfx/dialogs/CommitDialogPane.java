package com.jgitfx.jgitfx.dialogs;

import com.jgitfx.jgitfx.fileviewers.SelectableFileViewer;
import javafx.geometry.Orientation;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.Status;
import org.eclipse.jgit.lib.PersonIdent;
import org.reactfx.value.Val;

/**
 * A basic implementation of {@link CommitDialogPaneBaseOld}.
 */
public class CommitDialogPane extends CommitDialogPaneBaseOld<SelectableFileViewer> {

    // GUI components
    private final TextArea messageArea = new TextArea();
    @Override protected String getCommitMessage() { return messageArea.getText(); }

    private final CheckBox amendCheckBox = new CheckBox("Amend commit");
    @Override protected boolean isAmendCommit() { return amendCheckBox.isSelected(); }

    // TODO: implement GUI component for author
    @Override protected PersonIdent getAuthor() { return new PersonIdent("", ""); }

    // Layout Handlers
    private final BorderPane borderPane = new BorderPane();
    protected final BorderPane getBorderPane() { return borderPane; }

    private final SplitPane splitter = new SplitPane();
    protected final SplitPane getSplitter() { return splitter; }

    public CommitDialogPane(Val<Git> git, SelectableFileViewer fileViewer) {
        super(git, fileViewer, new ButtonType("Commit", ButtonBar.ButtonData.YES));

        getButtonTypes().addAll(ButtonType.CANCEL);

        splitter.setOrientation(Orientation.VERTICAL);
        splitter.getItems().addAll(
                // top
                fileViewer,

                // bottom
                new VBox(
                        new Label("Commit Message:"),
                        messageArea
                )
        );

        borderPane.setCenter(splitter);
        borderPane.setRight(new VBox(
                amendCheckBox
                // TODO: author content here
        ));
        setContent(borderPane);
    }

    @Override
    protected void displayFileViewer(Status status) {
        if (!splitter.getItems().contains(getFileViewer())) {
            splitter.getItems().set(0, getFileViewer());
        }
        getFileViewer().refreshTree(status);
    }

    @Override
    protected void displayPlaceHolder() {
        if (splitter.getItems().contains(getFileViewer())) {
            splitter.getItems().set(0, new StackPane(new Label("No changes detected...")));
        }
    }
}
