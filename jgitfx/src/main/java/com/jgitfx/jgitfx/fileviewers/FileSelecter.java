package com.jgitfx.jgitfx.fileviewers;

import javafx.beans.value.ObservableBooleanValue;
import org.eclipse.jgit.api.Status;

import java.util.List;

/**
 * An interface for a custom GUI component that displays all the files on which
 * an action can be applied and which of those files  the user has selected for
 * an action to actually be applied.
 */
public interface FileSelecter {

    /**
     *
     * @return true at least one file has been selected
     */
    boolean hasSelectedFiles();
    ObservableBooleanValue hasSelectedFilesProperty();

    /**
     * Refreshes the GUI component that displays the files when {@link Status#hasUncommittedChanges()}} returns true
     */
    void refreshTree(Status status);

    /**
     * @return the files that were selected and on which the user wants some action to be applied
     */
    List<String> getSelectedFiles();

}
