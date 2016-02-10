# JGitFX
JGitFX provides easy Git integration (via JGit) with your JavaFX application (view commit history, add Git menu items or buttons, etc.). This is NOT another Git Client.

## ToC
[Goals](#goals)
[License](#license)

## Goals
The goals of this project are to provide:
- `MenuItem`s configured to their corresponding Git command that can be easily added to an application's `Menu` or `MenuBar`
 - Create a new repository
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

## Thanks
Many thanks to the [jgit-cookbook] for the code snippets as it makes writing this library a heck of a lot easier!

## Licenses
Licensed under the [BSD 2-Clause license]
[JGit] is licensed under the [EDL].

[JGit]: http://eclipse.org/jgit/
[jgit-cookbook]: https://github.com/centic9/jgit-cookbook
[EDL]: http://www.eclipse.org/org/documents/edl-v10.php
[BSD 2-Clause License]: http://www.opensource.org/licenses/bsd-license.php