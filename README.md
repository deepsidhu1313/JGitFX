# JGitFX
JGitFX provides easy Git integration (via JGit) with your JavaFX application (view commit history, add Git menu items or buttons, etc.). This is NOT another Git Client.

## ToC

[Difficulty of JGIt](#difficulty-of-using-jgit)

[Principles of JGitFX](#principles-of-jgitfx)

[Roadmap](#roadmap)

[License](#license)

## Difficulty of using JGit

JGit currently allows a couple of levels of interaction with JGit:
- Level Zero - access JGit via a shell script
- Level One - embed it in a Java process and parse the results 
- Level Two - use the high-level porcelain commands via the `Git` class
- Level Three - use the low-level plumbing commands via the `Repository` class
- Level Four - interact with iterators: `RevWalk` and `TreeWalk`
- Level Five - interact with Git objects via `ObjectInserteer` and `ObjectReader`.

Level Two is the level that is used most of the time. However, there are times where a single `GitCommand` can be used to do multiple things. 
For example, let's look at `CheckoutCommand`. It can checkout a branch, create a new branch, and revert a modified tracked file back to the state it had in the most recent commit. So, `CheckoutCommand` has options for all of these. A developer looking to integrate JGit into their JavaFX application must sort through all the javadoc and figure out which options to use and which can be safely ignored. Although this isn't a difficult learning curve, it's still tedious and time-consuming. Isn't there a better way?

Furthermore, some Level 2 `GitCommand`s need to use Level 3 objects like `RevWalk` and `TreeWalk` to get the necessary arguments for simple commands. More confusion, errors, and problems ensue! 

## Principles of JGitFX

To help deal with this difficulty, JGitFX follows the following principles:

- Hide JGit code as much as possible, but expose it where customization may be needed.
- Let the developer focus more on the visuals (layout, GUI components to use), not the underlying JGit code implementation.
 
### Applying these principles

Wouldn't it be nice if a new developer could quickly use the correct commands and options to do the task they want? JGitFX has an answer for that: `GitHelper`. `GitHelper` is a class with "wrapper methods" that give the developer a list of methods that are clear in what they do and their required arguments for options needed by the underlying `GitCommand`. 

What about going through all the work of creating visual components (dialogs, buttons, fileviewers, etc.) that correctly handle the JGit code? One could waste a lot of time in failing to correctly implement the underlying JGit code. Fortunately, JGitFX provides default JGit handling inside of base classes whose layouts can be customized in a subclass.  

Thus, one can think of JGitFX using the following level-terminology:

- `GitHelper` = "Level 2, Sub-Level 1" 
- the base classes = "Level 2, Sub-Level 2" 
- the basic implementations of base classes = `Level 2, Sub-Level 3". 

## Roadmap

- [ ] Easily-addable `MenuItem`s
 - [x] Create local repository
 - [x] Open local repository
 - [ ] Commit: opens a commit dialog
 - [ ] Push: opens a push dialog
 - [ ] Pull: opens a pull dialog
- [ ] Useful, customizable `Dialog`s
 - [ ] Commit: choose which files to add and then commit
 - [ ] Merge: choose which branch to accept or merge a file by-hand
 - [ ] Compare: compare two versions of the same file
 - [ ] Push: choose which remote to which to push commits
 - [ ] Pull:
- [ ] Useful `Button`s that can be placed anywhere throughout the SceneGraph
 - [ ] commit
 - [ ] push
 - [ ] pull
 - [ ] fetch
- [ ] Useful views
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