# JGitFX
JGitFX provides easy Git integration (via JGit) with your JavaFX application (view commit history, add Git menu items or buttons, etc.). This is NOT another Git Client.

## ToC
[Roadmap](#roadmap)

[License](#license)

## Roadmap

[ ] Easily-addable `MenuItem`s
 - [x] Create local repository
 - [x] Open local repository
 - [ ] Commit (opens a commit dialog)
[ ] Useful, customizable `Dialog`s
 - [ ] Commit: choose which files to add and then commit
 - [ ] Merge: choose which branch to accept or merge a file by-hand
 - [ ] Compare: compare two versions of the same file
 - [ ] Push: choose which remote to which to push commits
 - [ ] Pull:
[ ] Useful `Button`s that can be placed anywhere throughout the SceneGraph
 - [ ] commit
 - [ ] push
 - [ ] pull
 - [ ] fetch
[ ] Useful views
 - [ ] Customizable Directory Watcher (watches filesystem for changes and uses colors to distinguish different files and their status: 
    Ignored, Unchanged, Modified (and staged), newly added (and staged),
 - [ ] A Commit History view that displays commit info, graphical branches, and file tree view showing which files were changed in a selected commit  

## Thanks
Many thanks to the [jgit-cookbook] for the code snippets as it makes writing this library a heck of a lot easier!

## Licenses
Licensed under the [BSD 2-Clause license]

[JGit] is licensed under the [EDL].

[JGit]: http://eclipse.org/jgit/
[jgit-cookbook]: https://github.com/centic9/jgit-cookbook
[EDL]: http://www.eclipse.org/org/documents/edl-v10.php
[BSD 2-Clause License]: http://www.opensource.org/licenses/bsd-license.php