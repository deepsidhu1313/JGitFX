package com.jgitfx.jgitfx.menus;

import javafx.scene.Node;
import javafx.stage.Window;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;

import java.io.File;
import java.io.IOException;
import java.util.function.Consumer;

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
        super("Open a Repository...", directory, graphic);
        setPostOpenRepository(afterOpeningConsumer);
        setOnAction(ae -> {
            File repoDir = createChooser("Open a Repository...").showDialog(window);
            if (repoDir != null && repoDir.toString().endsWith(".git")) {
                try {
                    Repository repo = new FileRepositoryBuilder()
                            .setGitDir(repoDir)
                            .readEnvironment()
                            .findGitDir()
                            .build();
                    Git git = new Git(repo);
                    postOpenRepository.accept(git);
                } catch (IOException ex) {
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
