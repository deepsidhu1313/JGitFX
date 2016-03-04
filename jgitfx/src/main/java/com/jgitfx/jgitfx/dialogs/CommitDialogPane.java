package com.jgitfx.jgitfx.dialogs;

import com.jgitfx.jgitfx.fileviewers.SelectableFileViewer;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import org.eclipse.jgit.api.AddCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.Status;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.reactfx.value.Val;

import static com.jgitfx.jgitfx.dialogs.GitButtonTypes.COMMIT;
import static javafx.scene.control.ButtonType.CANCEL;

/**
 * The content of {@link CommitDialog}.
 *
 * <p>Note: this DialogPane executes the code that commits the selected files. {@link CommitDialog} does not
 * handle this code.</p>
 */
public final class CommitDialogPane extends DialogPane {

    private final Val<Git> git;
    private Git getGitOrThrow() { return git.getOrThrow(); }

    private final TextArea messageArea = new TextArea();
    private final BorderPane root = new BorderPane();
    private final SelectableFileViewer fileViewer;
    private final CheckBox amendCommit = new CheckBox("Amend commit");

    public CommitDialogPane(Val<Git> git, Status status) {
        super();
        this.git = git;

        getButtonTypes().addAll(COMMIT, CANCEL);
        Button commitButton = (Button) lookupButton(COMMIT);
        commitButton.setOnAction(ae -> commitFiles());

        fileViewer = new SelectableFileViewer(status);
        root.setCenter(fileViewer);

        // commit button is disabled when file viewer has no selected files
        commitButton.disableProperty().bind(fileViewer.hasSelectedFilesProperty().not());

        setContent(root);
        root.setBottom(new VBox(
                new Label("Commit Message:"),
                messageArea
        ));
        root.setRight(new VBox(
                amendCommit
                // TODO: author content here
        ));
    }

    private void refreshTree() {
        try {
            fileViewer.refreshTree(getGitOrThrow().status().call());
        } catch (GitAPIException e) {
            e.printStackTrace();
        }
    }

    private void commitFiles() {
        try {
            AddCommand add = getGitOrThrow().add();
            fileViewer.getSelectedFiles().forEach(add::addFilepattern);
            add.call();

            getGitOrThrow().commit()
                    .setAllowEmpty(false)
                    .setAmend(amendCommit.isSelected())
                    .setMessage(messageArea.getText())
                    // .setAuthor(); // TODO implement author
                    .call();
        } catch (GitAPIException e) {
            e.printStackTrace();
        }
    }

}
