package com.jgitfx.jgitfx.fileviewers;

import com.jgitfx.jgitfx.GitFileStatus;
import com.jgitfx.jgitfx.ModifiedPath;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.BooleanProperty;
import javafx.scene.control.CheckBoxTreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.layout.Region;
import org.eclipse.jgit.api.Status;
import org.reactfx.util.Tuple2;
import org.reactfx.util.Tuples;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * SelectableFileViewer displays the files that have been added, removed, or changed since the previous commit and
 * allows the user to select the files on which the user wants some action done.
 *
 * <p>Shared directories are consolidated into the same directory. For example, if there are two files that have been
 * changed whose paths are {@code a/b/c/d/e/file1.txt} and {@code a/b/c/d/e/file2.txt}, then the TreeView will display
 * the files like so:</p>
 * <pre>
 *     {@code
 *     &gt; a/b/c/d/e
 *         &gt; file1.txt
 *         &gt; file2.txt
 *     }
 * </pre>
 *
 * <h2>Usages</h2>
 * <ul>
 *     <li>To determine if the user has selected any files at all, use {@link #hasSelectedFilesProperty()}</li>
 *     <li>When ready to apply some action on the selected files, get those files via {@link #getSelectedFiles()}</li>
 * </ul>
 */
public class SelectableFileViewer extends Region implements FileSelecter {

    private final TreeView<ModifiedPath> view = new TreeView<>();
    private final CheckBoxTreeItem<ModifiedPath> root = new CheckBoxTreeItem<>();

    private List<ModifiedPath> changedFiles;
    private List<Tuple2<ModifiedPath, BooleanProperty>> fileSelectionStates;

    /* *************** *
     * Constructor     *
     * *************** */

    public SelectableFileViewer(Status status) {
        super();
        getStyleClass().add("selectable-file-viewer");
        view.getStyleClass().add("selectable-file-tree-view");
        view.setRoot(root);
        view.setShowRoot(false);
        view.setCellFactory(GitFileStatusTreeCell.forTreeView());

        getChildren().add(view);

        refreshTree(status);
    }

    /* *************** *
     * Public Methods  *
     * *************** */

    public final boolean hasSelectedFiles() { return root.isIndeterminate() || root.isSelected(); }
    public final BooleanBinding hasSelectedFilesProperty() {
        return root.indeterminateProperty().or(root.selectedProperty());
    }

    /**
     * @return the list of files that were selected.
     */
    public final List<String> getSelectedFiles() {
        return Arrays.asList(
                fileSelectionStates.stream()
                // only get the paths that are checked
                .filter(v -> v.get2().get())
                // get the relative path as a String
                .map(v -> v.get1().getPath().toString())
                .toArray(String[]::new)
        );
    }

    public void refreshTree(Status status) {
        int totalSize = status.getAdded().size() + status.getChanged().size() + status.getMissing().size();

        changedFiles = new ArrayList<>(totalSize);
        // TODO: this probably doesn't account for files that were moved / renamed
        status.getAdded()  .forEach(file -> changedFiles.add(new ModifiedPath(Paths.get(file), GitFileStatus.ADDED)));
        status.getChanged().forEach(file -> changedFiles.add(new ModifiedPath(Paths.get(file), GitFileStatus.MODIFIED)));
        status.getMissing().forEach(file -> changedFiles.add(new ModifiedPath(Paths.get(file), GitFileStatus.REMOVED)));

        fileSelectionStates = new ArrayList<>(totalSize);

        buildRoot();
    }

    /* *************** *
     * Private Methods *
     * *************** */

    private void buildRoot() {
        root.getChildren().clear();

        for (ModifiedPath firstLevelPath : getNamesAtIndex(0)) {
            CheckBoxTreeItem<ModifiedPath> firstLevelItem = new CheckBoxTreeItem<>(firstLevelPath);
            if (changedFiles.contains(firstLevelPath)) {
                fileSelectionStates.add(Tuples.t(firstLevelPath, firstLevelItem.selectedProperty()));
            } else {
                buildTreeRecursively(firstLevelPath, firstLevelItem, 1);
            }
            selectAndExpandTreeItem(firstLevelItem);
            root.getChildren().add(firstLevelItem);
        }
    }


    // TODO Refactor this method into smaller methods
    /**
     * Builds a Tree of {@link CheckBoxTreeItem}s and consolidates any shared directories into one item.
     *
     * @param currentPath the {@link ModifiedPath} that goes from the firstLevelChild to one level above nameIndex
     * @param parent the {@link CheckBoxTreeItem} whose children to build
     * @param nameIndex the index of names within the {@link java.nio.file.Path} objects used to identify
     *                  directories that should be consolidated onto a single row.
     */
    private void buildTreeRecursively(ModifiedPath currentPath, CheckBoxTreeItem<ModifiedPath> parent, int nameIndex) {
        boolean consolidatingDirectories = true;
        while (consolidatingDirectories) {
            ModifiedPath[] parentToLeafNames = getNamesAtIndex(nameIndex);
            if (parentToLeafNames == null) {
                return;
            } else {
                if (parentToLeafNames.length == 1) {
                    // either need to consolidate directories or just add one leaf

                    currentPath = currentPath.resolve(parentToLeafNames[0]);
                    if (changedFiles.contains(currentPath)) {
                        // leaf, so exit loop
                        consolidatingDirectories = false;

                        // add item as a child
                        CheckBoxTreeItem<ModifiedPath> child = new CheckBoxTreeItem<>(parentToLeafNames[0]);
                        selectAndExpandTreeItem(child);
                        parent.getChildren().add(child);

                        // add its selected property
                        fileSelectionStates.add(Tuples.t(currentPath, child.selectedProperty()));
                    } else {
                        // single directory as child, so consolidate the two values into one item
                        // and continue loop
                        parent.setValue(currentPath);

                        // update the index to get the next level's names
                        nameIndex++;
                    }
                } else {
                    // multiple items found (leaf or directories) so exit loop
                    consolidatingDirectories = false;

                    for (ModifiedPath path : parentToLeafNames) {
                        ModifiedPath extendedPath = currentPath.resolve(path);

                        CheckBoxTreeItem<ModifiedPath> child = new CheckBoxTreeItem<>(path);
                        if (changedFiles.contains(extendedPath)) {
                            fileSelectionStates.add(Tuples.t(extendedPath, child.selectedProperty()));
                        } else {
                            buildTreeRecursively(extendedPath, child, nameIndex + 1);
                        }
                        selectAndExpandTreeItem(child);
                        parent.getChildren().add(child);
                    }
                }
            }
        }
    }

    private void selectAndExpandTreeItem(CheckBoxTreeItem<ModifiedPath> item) {
        item.setSelected(true);
        item.setExpanded(true);
    }

    private ModifiedPath[] getNamesAtIndex(int nameIndex) {
        return changedFiles.stream()
                // insure ModifiedFile#getName is given a valid index
                .filter(v -> v.getNameCount() >= nameIndex)
                // if a leaf is reached, status is file's status; otherwise, it's UNCHANGED
                .map(v -> v.getName(nameIndex))
                .distinct()
                .toArray(ModifiedPath[]::new);
    }

}
