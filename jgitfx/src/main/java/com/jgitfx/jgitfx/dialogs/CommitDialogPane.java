package com.jgitfx.jgitfx.dialogs;

import com.jgitfx.base.dialogs.CommitDialogPaneBase;
import com.jgitfx.jgitfx.fileviewers.SelectableFileViewer;
import java.util.List;
import javafx.beans.binding.Bindings;
import javafx.geometry.Orientation;
import javafx.scene.control.Button;
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
 * A basic implementation of {@link CommitDialogPaneBase}.
 */
public class CommitDialogPane extends CommitDialogPaneBase {

    // GUI components
    private final SelectableFileViewer fileViewer;
    public List<String> getSelectedFiles() { return fileViewer.getSelectedFiles(); }

    private final TextArea messageArea = new TextArea();
    public String getCommitMessage() { return messageArea.getText(); }

    private final CheckBox amendCheckBox = new CheckBox("Amend commit");
    public boolean isAmendCommit() { return amendCheckBox.isSelected(); }

    // TODO: implement GUI component for author
    public PersonIdent getAuthor() { return new PersonIdent("", ""); }

    // TODO: implement GUI component for committer
    public PersonIdent getCommitter() { return new PersonIdent("", ""); }

    // Layout Handlers
    private final BorderPane borderPane = new BorderPane();
    private final SplitPane splitter = new SplitPane();

    public CommitDialogPane(Val<Git> git, SelectableFileViewer fileViewer, ButtonType commitButton) {
        super(git);
        this.fileViewer = fileViewer;

        getButtonTypes().addAll(commitButton, ButtonType.CANCEL);
        Button button = (Button) lookupButton(commitButton);
        // commit button is disabled when there are no selected files
        button.disableProperty().bind(Bindings.not(fileViewer.hasSelectedFilesProperty()));

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
                // TODO: author & committer GUI components here
        ));
        setContent(borderPane);
    }

    public void displayFileViewer(Status status) {
        if (!splitter.getItems().contains(fileViewer)) {
            splitter.getItems().set(0, fileViewer);
        }
        fileViewer.refreshTree(status);
    }

    public void displayPlaceHolder() {
        if (splitter.getItems().contains(fileViewer)) {
            splitter.getItems().set(0, new StackPane(new Label("No changes detected...")));
        }
    }
}
