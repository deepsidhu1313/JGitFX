package com.jgitfx.jgitfx.menus;

import javafx.scene.Node;
import javafx.stage.Window;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;

import java.io.File;
import java.util.function.Consumer;

import static com.jgitfx.jgitfx.GitActions.createRepoIn;

/**
 * CreateRepoMenuItem is a MenuItem that will create a new repository and
 * return its output as a {@link Git} object when clicked via its {@link #postRepositoryCreation}.
 */
public class CreateRepoMenuItem extends RepoMenuItemBase {

    /**
     * The Consumer for the created repository as a {@link Git} object that is returned
     */
    private Consumer<Git> postRepositoryCreation;
    public final Consumer<Git> getPostRepositoryCreation() { return postRepositoryCreation; }
    public final void setPostRepositoryCreation(Consumer<Git> consumer) { postRepositoryCreation = consumer; }

    /**
     * Creates a CreateRepoMenuItem that, when clicked, will create a new repository in the returned directory.
     * If the directory is "/home/userName/Directory", the created repository's path will be
     * "/home/userName/Directory/.git"
     *
     * @param window the window used for {@link javafx.stage.DirectoryChooser#showDialog(Window)}. Usually, the
     *               Application's primaryStage.
     * @param directory the initial directory to display in the {@link javafx.stage.DirectoryChooser}.
     * @param afterCreationConsumer the consumer to call when a repository is created
     *                              in the form of a {@link Git} object.
     */
    public CreateRepoMenuItem(Window window, File directory, Consumer<Git> afterCreationConsumer,
                              String text, Node graphic) {
        super(directory, text, graphic);
        setPostRepositoryCreation(afterCreationConsumer);
        setOnAction(ae -> {
            File parentDir = createChooser("Choose the parent directory of the new Repository").showDialog(window);
            if (parentDir != null) {
                try {
                    Git git = createRepoIn(parentDir);
                    postRepositoryCreation.accept(git);
                } catch (GitAPIException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * Constructs a CreateRepoMenuItem with the given text and no graphic.
     */
    public CreateRepoMenuItem(Window window, File directory, Consumer<Git> afterCreationConsumer, String text) {
        this(window, directory, afterCreationConsumer, text, null);
    }

    /**
     * Constructs a CreateRepoMenuItem with the text "Create a new Repository..." and no graphic.
     */
    public CreateRepoMenuItem(Window window, File directory, Consumer<Git> afterCreationConsumer) {
        this(window, directory, afterCreationConsumer, "Create a new Repository...");

    }

}
