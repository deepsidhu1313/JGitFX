package com.jgitfx.demos;

import com.jgitfx.jgitfx.menus.CreateRepoMenuItem;
import com.jgitfx.jgitfx.menus.OpenRepoMenuItem;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import org.eclipse.jgit.api.Git;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class TextEditorWithGit extends Application {

    public static final File TEST_REPO_DIR;
    static {
        Path p = Paths.get(System.getProperty("user.home"), "JGitFXTestRepo");
        if (Files.notExists(p)) {
            try {
                p = Files.createDirectory(p);
            } catch (IOException e) {
                System.out.println("Error in attempt to create the TEST_REPO_DIR...");
                e.printStackTrace();
            }
        }
        TEST_REPO_DIR = p.toFile();
        assert TEST_REPO_DIR.isDirectory();
        assert TEST_REPO_DIR.exists();
    }

    Stage stage;
    TextArea area;
    BorderPane root = new BorderPane();

    // The Git object for High Porcelain git commands
    private Git git;
    private void setGit(Git g) {
        System.out.println("Setting Git to: " + g.toString());
        git = g;
    }

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

        Scene scene = new Scene(root, 500, 500);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private Menu initFileMenu() {
        Menu fileMenu = new Menu("File");
        MenuItem exit = new MenuItem("Exit");
        exit.setOnAction(ae -> Platform.exit());
        fileMenu.getItems().add(exit);
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
        Menu gitMenu = new Menu("Git");
        gitMenu.getItems().addAll(
                // Repository creation
                createRepo,
                openRepo,
                new SeparatorMenuItem(),

                // basic commit
                new MenuItem("Add File"),
                new MenuItem("Commit"),
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
