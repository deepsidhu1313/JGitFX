package com.jgitfx.jgitfx.dialogs;

import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;

/**
 * {@link ButtonType}s for Git-related commands
 */
public class GitButtonTypes {

    public static final ButtonType COMMIT   = new ButtonType("Commit",    ButtonBar.ButtonData.YES);
    public static final ButtonType MERGE    = new ButtonType("Merge",     ButtonBar.ButtonData.YES);
    public static final ButtonType REVERT   = new ButtonType("Revert",    ButtonBar.ButtonData.YES);

}