package com.jgitfx.jgitfx;

import java.nio.file.Path;
import java.util.Objects;

/**
 * ModifiedPath is a wrapper class that holds a {@link Path} and its {@link GitFileStatus}. Most methods within
 * the class are convenience methods for accessing or somehow using its {@code path}.
 */
public class ModifiedPath {

    private final Path path;
    public final Path getPath() { return path; }

    private final GitFileStatus status;
    public final GitFileStatus getStatus() { return status; }

    public ModifiedPath(Path path, GitFileStatus status) {
        this.path = path;
        this.status = status;
    }

    public ModifiedPath resolve(ModifiedPath other) {
        return new ModifiedPath(path.resolve(other.getPath()), other.status);
    }

    public ModifiedPath getName(int nameIndex) {
        return new ModifiedPath(path.getName(nameIndex), nameIndex == getNameCount() - 1 ? status : GitFileStatus.UNCHANGED);
    }

    public int getNameCount() {
        return path.getNameCount();
    }

    public Path getLastName() {
        return path.getName(getNameCount());
    }

    public boolean lastNameMatches(ModifiedPath other) { return getLastName().equals(other.getLastName()); }

    public boolean endsWith(ModifiedPath other) { return path.endsWith(other.getPath()); }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (path == null ? 0 : path.hashCode()) + (status == null ? 0 : status.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (!(obj instanceof ModifiedPath))
            return false;
        ModifiedPath other = (ModifiedPath) obj;
        return Objects.equals(path, other.getPath()) &&
                Objects.equals(status, other.getStatus());
    }

    @Override
    public String toString() {
        return "ModifiedPath(path: [" + path.toString() + "], status: [" + status.toString() + "])";
    }
}
