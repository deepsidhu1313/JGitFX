# JGitFX
JGitFX provides easy Git integration (via JGit) with your JavaFX application (view commit history, add Git menu items or buttons, etc.). This is NOT another Git Client.

# Goals
The goals of this project are to provide:
- A default `Menu` containing all the Git options one wants as its `MenuItem`s that can be easily added to an applicatoni's `MenuBar`
 - create a new repository
 - push, pull, commit, etc.
- Git-specific `Button`s that can be placed anywhere throughout the SceneGraph
 - push, pull, commit
 - merge, rebase, cherry-pick
 - reset, revert, etc.
- A Commit History view that displays
 - the commits (message, author, date, hash)
 - the graphical branches and their connections/history
 - a details pane that displays more information about the selected commit
 - a file tree view that displays which files were changed
- A merge / compare dialog used for merging/comparing  

# License
Licensed under the BSD-Clause-2 license
