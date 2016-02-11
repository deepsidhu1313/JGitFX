package com.jgitfx.demos;

import com.jgitfx.jgitfx.menus.AddFilesMenuItem;
import com.jgitfx.jgitfx.menus.CommitMenuItem;
import com.jgitfx.jgitfx.menus.CreateRepoMenuItem;
import com.jgitfx.jgitfx.menus.OpenRepoMenuItem;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.eclipse.jgit.api.Git;
import org.reactfx.value.Var;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class TextEditorWithGit extends Application {

    public static final File TEST_REPO_DIR;
    static {
        Path testRepoParentDir = Paths.get(System.getProperty("user.home"), "JGitFXTestRepo");

        if (Files.notExists(testRepoParentDir)) {
            try {
                testRepoParentDir = Files.createDirectory(testRepoParentDir);
            } catch (IOException e) {
                System.out.println("Error in attempt to create the TEST_REPO_DIR...");
                e.printStackTrace();
            }
        }
        TEST_REPO_DIR = testRepoParentDir.toFile();
        assert TEST_REPO_DIR.isDirectory();
        assert TEST_REPO_DIR.exists();
    }

    Stage stage;
    BorderPane root = new BorderPane();
    TextArea area;
    ScrollPane scrollPane;
    {
        VBox box = new VBox();
        scrollPane = new ScrollPane(box);
        scrollPane.setMinHeight(200);
        // quick and dirty way to set guide's text as file's content

        try {
            URI uri = this.getClass().getResource("text-editor-guide.txt").toURI();
            Files.lines(new File(uri).toPath()).forEach(lineText -> box.getChildren().add(new Text(lineText)));
        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
        }
    }

    // The Git object for High Porcelain git commands
    private Var<Git> git = Var.newSimpleVar(null);
    private void setGit(Git g) {
        System.out.println("Setting Git to: " + g.toString());
        git.setValue(g);
    }
    private Git getGit() { return git.getValue(); }
    private Var<Git> gitProperty() { return git; }

    private final Var<List<String>> selectedFiles = Var.newSimpleVar(null);
    public final List<String> getSelectedFiles() { return selectedFiles.getValue(); }
    public final void setSelectedFiles(List<String> value) { selectedFiles.setValue(value); }
    public final Var<List<String>> selectedFilesProperty() { return selectedFiles; }

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        stage = primaryStage;

        /*
            BorderPane {
                Top {
                    MenuBar {
                        File {
                            Exit - closes program
                        }
                        Git {
                            // code yet to be implemented
                        }
                    }
                }
                Center {
                    TextArea - the plaintext Text editor
                }
            }
        */
        area = new TextArea();

        // set up MenuBar, its Menus, and
        Menu fileMenu = initFileMenu();
        Menu gitMenu = initGitMenu(primaryStage);
        MenuBar menuBar = new MenuBar(fileMenu, gitMenu);

        root.setTop(menuBar);
        root.setCenter(area);
        root.setBottom(scrollPane);

        Scene scene = new Scene(root, 800, 800);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private Menu initFileMenu() {
        MenuItem createRandomFiles = new MenuItem("Create Random Files");
        createRandomFiles.setOnAction(ae -> {
            Random r = new Random();
            String numsAndLetters = "abcdefghijklmnopqrstuvwxyz0123456789";
            int randomMax = numsAndLetters.length() - 1;

            int length = 8;
            for (int count = 0; count < 5; count++) {
                StringBuilder sb = new StringBuilder();
                for (int index = 0; index < length; index++) {
                    sb.append(numsAndLetters.charAt(r.nextInt(randomMax)));
                }
                String randomFile = sb.toString();
                try {
                    Files.createTempFile(TEST_REPO_DIR.toPath(), randomFile, ".txt");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        });

        // since I don't know how to set up a directory watcher with all its complexity and updating and context menus,
        // here's a simple way to change which files are selected so that Git::Add Files can be demonstrated
        MenuItem changeSelection = new MenuItem("Change selected files");
        changeSelection.setOnAction(ae -> {
            FileChooser chooser = new FileChooser();
            chooser.setInitialDirectory(TEST_REPO_DIR);
            List<File> files = chooser.showOpenMultipleDialog(stage);
            if (files != null && !files.isEmpty()) {
                List<String> fileNames = files.stream().map(File::getName).collect(Collectors.toList());
                System.out.println("Files Names are: " + fileNames.toString());
                selectedFiles.setValue(fileNames);
            }
        });

        MenuItem exit = new MenuItem("Exit");
        exit.setOnAction(ae -> Platform.exit());

        Menu fileMenu = new Menu("File");
        fileMenu.getItems().addAll(createRandomFiles, changeSelection, exit);
        return fileMenu;
    }

    private Menu initGitMenu(Stage stage) {
        CreateRepoMenuItem createRepo = new CreateRepoMenuItem(
                stage,
                TEST_REPO_DIR,
                this::setGit
        );
        OpenRepoMenuItem openRepo = new OpenRepoMenuItem(
                stage,
                TEST_REPO_DIR,
                this::setGit
        );

        AddFilesMenuItem addFiles = new AddFilesMenuItem(git, selectedFiles);
        CommitMenuItem commit = new CommitMenuItem(git, (revCommit -> System.out.println("Commited: " + revCommit)));
        Menu gitMenu = new Menu("Git");
        gitMenu.getItems().addAll(
                // Repository creation
                createRepo,
                openRepo,
                new SeparatorMenuItem(),

                // basic commit
                addFiles,
                commit,
                new SeparatorMenuItem(),

                // merge/combine related
                new MenuItem("Merge"),
                new MenuItem("Cherry-Pick"),
                new MenuItem("Rebase"),
                new SeparatorMenuItem(),

                // remote related
                new MenuItem("Push"),
                new MenuItem("Pull"),
                new SeparatorMenuItem(),

                // mistake/error related
                new MenuItem("Revert"),
                new MenuItem("Reset")
        );
        return gitMenu;
    }
}
