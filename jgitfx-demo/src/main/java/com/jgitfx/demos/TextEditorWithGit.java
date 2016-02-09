package com.jgitfx.demos;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class TextEditorWithGit extends Application {

    TextArea area;
    BorderPane root = new BorderPane();

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {

        // Set up GUI, see multi-line comment for a visual idea
        /*
            BorderPane {
                top {
                    GitMenu {

                    }
                }
                center {
                    textArea
                }
            }
         */
        area = new TextArea();

        // set up MenuBar, its Menus, and
        Menu fileMenu = new Menu("File");
        MenuItem exit = new MenuItem("Exit");
        exit.setOnAction(ae -> Platform.exit());
        fileMenu.getItems().add(exit);

        Menu gitMenu = new Menu("Git");
        initGitMenu(gitMenu);
        MenuBar menuBar = new MenuBar(fileMenu, gitMenu);

        root.setTop(menuBar);
        root.setCenter(area);
        Scene scene = new Scene(root, 500, 500);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void initGitMenu(Menu gitMenu) {

    }
}
