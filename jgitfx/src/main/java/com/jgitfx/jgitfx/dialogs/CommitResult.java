package com.jgitfx.jgitfx.dialogs;

import org.eclipse.jgit.revwalk.RevCommit;

import java.util.List;

/**
 * Stores relevant information after a successful commit that developers can use
 * to update other GUI components more efficiently.
 */
public class CommitResult {

    private final List<String> affectedFiles;
    public final List<String> getAffectedFiles() { return affectedFiles; }

    private final RevCommit commit;
    public final RevCommit getCommit() { return commit; }

    public CommitResult(List<String> affectedFiles, RevCommit commit) {
        this.affectedFiles = affectedFiles;
        this.commit = commit;
    }
}
