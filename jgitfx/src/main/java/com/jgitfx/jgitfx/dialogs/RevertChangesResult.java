package com.jgitfx.jgitfx.dialogs;

import java.util.List;

/**
 * Stores relevant information after a reverting selected files back to their last snapshot in most recent commit
 * so that developers can use this info to update other GUI components more efficiently.
 */
public class RevertChangesResult {

    private final List<String> affectedFiles;
    public final List<String> getAffectedFiles() { return affectedFiles; }

    public RevertChangesResult(List<String> affectedFiles) {
        this.affectedFiles = affectedFiles;
    }
}
