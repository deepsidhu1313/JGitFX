package com.jgitfx.jgitfx.fileviewers;

import com.jgitfx.jgitfx.ModifiedPath;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.css.PseudoClass;
import javafx.scene.control.CheckBoxTreeItem;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.control.cell.CheckBoxTreeCell;
import javafx.util.Callback;
import javafx.util.StringConverter;

/**
 * The CSS-styleable TreeCellFactory used in {@link SelectableFileTreeView}.
 *
 * <p>{@link com.jgitfx.jgitfx.GitFileStatus} is used to determine the state of a file. Thus, there are currently
 * four ways the text's color can be styled:</p>
 * <pre>
 *     {@code
 *     .git-file-status-tree-cell:unchanged {
 *          // directories
 *         -fx-text-fill: black;
 *     }
 *     .git-file-status-tree-cell:added {
 *          // new files that were added to repository
 *         -fx-text-fill: green;
 *     }
 *     .git-file-status-tree-cell:modified {
 *          // tracked files that have been modified
 *         -fx-text-fill: blue;
 *     }
 *     .git-file-status-tree-cell:removed {
 *          // tracked files that have been deleted
 *         -fx-text-fill: gray;
 *     }
 *     }
 * </pre>
 */
public class GitFileStatusTreeCell extends CheckBoxTreeCell<ModifiedPath> {

    public static Callback<TreeView<ModifiedPath>, TreeCell<ModifiedPath>> forTreeView() {
        return (treeView) -> new GitFileStatusTreeCell();
    }

    public final static StringConverter<TreeItem<ModifiedPath>> CONVERTER = new StringConverter<TreeItem<ModifiedPath>>() {
        @Override
        public String toString(TreeItem<ModifiedPath> object) {
            return object.getValue().getPath().toString();
        }

        @Override
        public TreeItem<ModifiedPath> fromString(String string) {
            return null;
        }
    };

    private static PseudoClass UNCHANGED = PseudoClass.getPseudoClass("unchanged");
    private static PseudoClass ADDED = PseudoClass.getPseudoClass("added");
    private static PseudoClass MODIFIED = PseudoClass.getPseudoClass("modified");
    private static PseudoClass REMOVED = PseudoClass.getPseudoClass("removed");

    private final BooleanProperty unchanged;
    private final BooleanProperty added;
    private final BooleanProperty modified;
    private final BooleanProperty removed;

    public GitFileStatusTreeCell() {
        super(
                treeItem -> ((CheckBoxTreeItem) treeItem).selectedProperty(),
                CONVERTER
        );

        getStyleClass().add("git-file-status-tree-cell");

        // set up pseudo style classes
        unchanged   = new SimpleBooleanProperty(false);
        added       = new SimpleBooleanProperty(false);
        modified    = new SimpleBooleanProperty(false);
        removed     = new SimpleBooleanProperty(false);

        unchanged   .addListener(c -> pseudoClassStateChanged(UNCHANGED,    unchanged.get()));
        added       .addListener(c -> pseudoClassStateChanged(ADDED,        added.get()));
        modified    .addListener(c -> pseudoClassStateChanged(MODIFIED,     modified.get()));
        removed     .addListener(c -> pseudoClassStateChanged(REMOVED,      removed.get()));
    }

}
