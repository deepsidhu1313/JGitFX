package com.jgitfx.jgitfx.menus;

import javafx.scene.Node;
import javafx.scene.control.MenuItem;
import javafx.stage.DirectoryChooser;

import java.io.File;

/**
 * Base class used for Repository-related MenuItems such as {@link CreateRepoMenuItem}
 * and {@link OpenRepoMenuItem}; essentially, provides the convenient
 * {@link #createChooser(String)} method and its related {@link #initialDirectory} bean.
 */
public abstract class RepoMenuItemBase extends MenuItem {

    /**
     * The directory in which a {@link DirectoryChooser} should initially open when choosing
     * a Git repository.
     */
    private File initialDirectory;
    public final File getInitialDirectory() { return initialDirectory;}
    public final void setInitialDirectory(File directory) { initialDirectory = directory; }

    /**
     * Constructs a MenuItem and sets the initial directory
     * @param menuText the menuItem's text
     * @param directory the directory to which to set {@link #initialDirectory}
     * @param graphic the graphic to display to the left of the menuItem's text
     */
    public RepoMenuItemBase(String menuText, File directory, Node graphic) {
        super(menuText, graphic);
        setInitialDirectory(directory);
    }

    /**
     * Constructs a RepoMenuItem without a graphic
     * @param menuText the menuItem's text
     * @param directory the directory to which to set {@link #initialDirectory}
     */
    public RepoMenuItemBase(String menuText, File directory) {
        this(menuText, directory, null);
    }

    /**
     * Convenience method for creating a {@link DirectoryChooser}
     * @param chooserTitle the Title to set for the chooser
     * @return a DirectoryChooser with the set title and initial directory of {@link #getInitialDirectory()}
     */
    protected final DirectoryChooser createChooser(String chooserTitle) {
        DirectoryChooser chooser = new DirectoryChooser();
        chooser.setTitle(chooserTitle);
        chooser.setInitialDirectory(getInitialDirectory());
        return chooser;
    }

}
