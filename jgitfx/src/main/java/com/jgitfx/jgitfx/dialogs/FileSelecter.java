package com.jgitfx.jgitfx.dialogs;

import javafx.beans.value.ObservableBooleanValue;
import org.eclipse.jgit.api.Status;

import java.util.List;


public interface FileSelecter {

    boolean hasSelectedFiles();
    ObservableBooleanValue hasSelectedFilesProperty();

    void refreshTree(Status status);

    List<String> getSelectedFiles();

}
