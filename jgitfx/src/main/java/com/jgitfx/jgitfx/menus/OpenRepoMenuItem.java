package com.jgitfx.jgitfx.menus;

import javafx.scene.Node;
import javafx.stage.Window;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;

import java.io.File;
import java.util.function.Consumer;

import static com.jgitfx.jgitfx.GitActions.openRepo;

/**
 * OpenRepoMenuItem is a MenuItem that will open a repository from local storage and return
 * that opened repository as a {@link Git} object via its {@link #postOpenRepository}.
 */
public class OpenRepoMenuItem extends RepoMenuItemBase {

    /**
     * The Consumer for the opened repository as a {@link Git} object that is returned
     */
    private Consumer<Git> postOpenRepository;
    public Consumer<Git> getPostOpenRepository() { return postOpenRepository; }
    public void setPostOpenRepository(Consumer<Git> consumer) { postOpenRepository = consumer; }

    /**
     * Creates an OpenRepoMenuItem
     * @param window the window to use in {@link javafx.stage.DirectoryChooser#showDialog(Window)}
     * @param afterOpeningConsumer the consumer called with the returned {@link Git} object
     */
    public OpenRepoMenuItem(Window window, File directory, Consumer<Git> afterOpeningConsumer, Node graphic) {
        super(directory, "Open a Repository...", graphic);
        setPostOpenRepository(afterOpeningConsumer);
        setOnAction(ae -> {
            File gitRepoDir = createChooser("Open a Repository ('.git' folder)...").showDialog(window);
            if (gitRepoDir != null && gitRepoDir.toString().endsWith(".git")) {
                try {
                    Git git = openRepo(gitRepoDir);
                    postOpenRepository.accept(git);
                } catch (GitAPIException ex) {
                    ex.printStackTrace();
                }
            }
        });
    }

    /**
     * Same as {@link #OpenRepoMenuItem(Window, File, Consumer, Node)} except without a graphic
     */
    public OpenRepoMenuItem(Window window, File directory, Consumer<Git> afterOpeningFunction) {
        this(window, directory, afterOpeningFunction, null);
    }
}
