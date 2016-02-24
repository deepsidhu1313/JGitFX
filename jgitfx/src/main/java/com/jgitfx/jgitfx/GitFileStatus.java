package com.jgitfx.jgitfx;

/**
 * The status of a file in the repository
 */
public enum GitFileStatus {
    /** Files that have not been changed or are directories */
    UNCHANGED,
    /** Untracked files that have been added to the repo */
    ADDED,
    /** Tracked files that have been modified */
    MODIFIED,
    /** Tracked files that have been deleted */
    REMOVED
}
