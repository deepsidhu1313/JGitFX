package com.jgitfx.base;

import java.io.File;
import java.util.List;
import org.eclipse.jgit.api.AddCommand;
import org.eclipse.jgit.api.CheckoutCommand;
import org.eclipse.jgit.api.CloneCommand;
import org.eclipse.jgit.api.CreateBranchCommand;
import org.eclipse.jgit.api.FetchCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.ListBranchCommand;
import org.eclipse.jgit.api.MergeCommand;
import org.eclipse.jgit.api.MergeResult;
import org.eclipse.jgit.api.PullCommand;
import org.eclipse.jgit.api.PullResult;
import org.eclipse.jgit.api.PushCommand;
import org.eclipse.jgit.api.ResetCommand;
import org.eclipse.jgit.api.RevertCommand;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.AnyObjectId;
import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.lib.PersonIdent;
import org.eclipse.jgit.lib.ProgressMonitor;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.merge.MergeStrategy;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.transport.FetchResult;
import org.eclipse.jgit.transport.PushResult;
import org.eclipse.jgit.transport.RefSpec;
import org.eclipse.jgit.transport.TagOpt;

/**
 * GitHelper provides static methods to make it easier to call the correct Git command with the correct option.
 *
 * <p>The high porcelain {@link Git} object makes interacting with the underlying repository much easier. However,
 * the Git commands must still account for all options. Often times, to do just one action, it becomes uncertain
 * which options need to be set, which can be ignored, and which should appear in one situation but not in another.</p>
 *
 * <p>Thus, this helper class makes it easier to do a simple task by providing clearly-named methods, the
 * correct arguments needed, and clear javadoc to help the developer decide which to use.</p>
 */
public class GitHelper {

    // prevent instantiation
    private GitHelper() {}

    /* ************************* *
     * Initialization
     * ************************* */

    /**
     * Creates a new Git repository in the parent directory
     * @param parentDirectory the directory in which to create the new Git repository
     * @return a high-porcelain Git object
     * @throws GitAPIException
     */
    public static Git createRepoIn(File parentDirectory) throws GitAPIException {
        return Git.init().setDirectory(parentDirectory).call();
    }

    /**
     * Opens a local Git repository
     * @param gitMetaDirectory the ".git" directory to open
     * @return a high-porcelain Git object
     * @throws GitAPIException
     */
    public static Git openRepo(File gitMetaDirectory) throws GitAPIException {
        return Git.init().setGitDir(gitMetaDirectory).call();
    }

    /**
     * Clones a git repository using the given uri and stores it in the parent directory. Checks out the branch
     * to which the remote HEAD currently points.
     * @param cloneURI the uri to the remote repository
     * @param parentDirectory the directory in which to store the git meta directory (".git" directory)
     * @return the cloned Git repository
     * @throws GitAPIException
     */
    public static Git cloneRepo(String cloneURI, File parentDirectory) throws GitAPIException {
        CloneCommand clone = Git.cloneRepository();
        return Git.cloneRepository()
                // essential
                .setURI(cloneURI)               // essential
                .setDirectory(parentDirectory)  // parent directory to store git dir
                .call();
    }

    /**
     * Clones a git repository using the given uri and stores it in the parent directory. Checks out the
     * given reference or (if value is null) does not check out a branch
     * (which reduces time needed to complete command).
     * @param cloneURI the uri to the remote repository
     * @param parentDirectory the directory in which to store the git meta directory (".git" directory)
     * @param checkoutRef the ref name ("refs/heads/master"), branch name ("master") or tag name ("v1.2.3"). If
     *                    {@code null} is passed, will not checkout a branch.
     * @return the clones Git repository
     * @throws GitAPIException
     */
    public static Git cloneRepo(String cloneURI, File parentDirectory, String checkoutRef) throws GitAPIException {
        CloneCommand clone = Git.cloneRepository();

        if (checkoutRef == null) {
            clone.setNoCheckout(true);
        } else {
            clone.setBranch(checkoutRef);
        }

        return clone
                .setURI(cloneURI)
                .setDirectory(parentDirectory)
                .call();
    }

    /**
     *
     * Clones a git repository using the given uri and stores it in the parent directory. Checks out the
     * given reference or (if value is null) does not check out a branch
     * (which reduces time needed to complete command).
     * @param cloneURI the uri to the remote repository
     * @param parentDirectory the directory in which to store the git meta directory (".git" directory)
     * @param checkoutRef the ref name ("refs/heads/master"), branch name ("master") or tag name ("v1.2.3"). If
     *                    {@code null} is passed, will not checkout a branch.
     * @param branchesToClone the branches to clone or all branches if passed a {@code null} value.
     * @param monitor reports the progress of the clone command; can be null
     * @return the cloned Git repository
     * @throws GitAPIException
     */
    public static Git cloneRepo(String cloneURI, File parentDirectory, String remoteName,
                                String checkoutRef, List<String> branchesToClone, ProgressMonitor monitor) throws GitAPIException {
        CloneCommand clone = Git.cloneRepository();

        if (checkoutRef == null) {
            clone.setNoCheckout(true);
        } else {
            clone.setBranch(checkoutRef);
        }

        if (branchesToClone == null) {
            clone.setCloneAllBranches(true);
        } else {
            clone.setBranchesToClone(branchesToClone);
        }

        if (monitor != null) { clone.setProgressMonitor(monitor); }

        return clone
                .setURI(cloneURI)
                .setDirectory(parentDirectory)
                .setRemote(remoteName)
                .call();
    }

    /* ************************* *
     * Adding & Committing
     * ************************* */

    /**
     * Add (stage) files to the index (includes previously untracked files that will now be tracked files after call finishes).
     * @param git the git repository
     * @param relativePaths the relative paths of the files to add
     * @throws GitAPIException
     */
    public static void addFiles(Git git, List<String> relativePaths) throws GitAPIException {
        addFiles(git, relativePaths, false);
    }

    /**
     * Add (stage) files to the index.
     * @param git the git repository
     * @param relativePaths the relative paths of the files to add
     * @param excludeNewFiles if true, any untracked files in {@code relativePaths} will not be added (they won't
     *                        become "tracked" at the end of the call).
     * @throws GitAPIException
     */
    public static void addFiles(Git git, List<String> relativePaths, boolean excludeNewFiles) throws GitAPIException {
        AddCommand adder = git.add();
        adder.setUpdate(excludeNewFiles);
        relativePaths.forEach(adder::addFilepattern);
        adder.call();
    }

    /**
     * Commits the files that were added (staged).
     * @param git the git repo
     * @param amendCommit if true, the previous commit will be amended by this one
     * @param message commit message
     * @param author commit author. If this value is {@code null}, then two possibilities arise. If {@code amendCommit}
     *               is true, then defaults to the previous commit's author. If that option is false, defaults to
     *               config info in repository.
     * @return the commit
     * @throws GitAPIException
     */
    public static RevCommit commitFiles(
            Git git, boolean amendCommit, String message, PersonIdent author) throws GitAPIException {
        return commitFiles(git, amendCommit, message, author, null);
    }

    /**
     * Commits the files that were added (staged).
     * @param git the git repo
     * @param amendCommit if true, the previous commit will be amended by this one
     * @param message commit message
     * @param author commit author. If this value is {@code null}, then two possibilities arise. If {@code amendCommit}
     *               is true, then defaults to the previous commit's author. If that option is false, defaults to
     *               {@code committer}
     * @param committer used as the author when conditions listed in {@code author} param are met. If these conditions
     *                  are met and {@code committer} is null, then defaults to config info in repository
     * @return the commit
     * @throws GitAPIException
     */
    public static RevCommit commitFiles(
            Git git, boolean amendCommit, String message, PersonIdent author, PersonIdent committer) throws GitAPIException {
        return git.commit()
                .setAllowEmpty(false)       // don't allow empty commit: a commit that changes nothing
                .setAmend(amendCommit)      // whether commit is amending previous one or not
                .setMessage(message)
                .setAuthor(author)
                .setCommitter(committer)
                .call();
    }

    /* ************************************ *
     * Merging, Rebasing & Cherry-Picking
     * ************************************ */

    private static void setupMergeCommand(MergeCommand merge, List<Ref> commitsByRef, List<AnyObjectId> commitsById,
                                          List<NamedCommit> commitsByNameAndId, ProgressMonitor monitor,
                                          MergeStrategy strategy, MergeCommand.FastForwardMode fastForwardMode) {
        commitsByRef.forEach(merge::include);
        commitsById.forEach(merge::include);
        commitsByNameAndId.forEach(nc -> merge.include(nc.getName(), nc.getObjectId()));

        if (monitor != null) { merge.setProgressMonitor(monitor); }

        merge
                .setFastForward(fastForwardMode)
                .setStrategy(strategy);
    }

    public static MergeResult mergeWithSquash(Git git, MergeStrategy strategy,  List<Ref> commitsByRef,
                                       List<AnyObjectId> commitsById, List<NamedCommit> commitsByNameAndId,
                                       MergeCommand.FastForwardMode fastForwardMode,
                                       ProgressMonitor monitor) throws GitAPIException {
        MergeCommand merge = git.merge();

        setupMergeCommand(merge, commitsByRef, commitsById, commitsByNameAndId, monitor, strategy, fastForwardMode);

        return merge
                .setStrategy(strategy)
                .setFastForward(fastForwardMode)
                .setSquash(true)
                .call();
    }

    public static MergeResult mergeWithoutCommit(Git git, MergeStrategy strategy, List<Ref> commitsByRef,
                                          List<AnyObjectId> commitsById, List<NamedCommit> commitsByNameAndId,
                                          MergeCommand.FastForwardMode fastForwardMode,
                                          ProgressMonitor monitor) throws GitAPIException {
        MergeCommand merge = git.merge();

        setupMergeCommand(merge, commitsByRef, commitsById, commitsByNameAndId, monitor, strategy, fastForwardMode);

        return merge
                .setStrategy(strategy)
                .setFastForward(fastForwardMode)
                .setCommit(false)
                .call();
    }

    public static MergeResult mergeWithCommit(Git git, MergeStrategy strategy, List<Ref> commitsByRef, List<AnyObjectId> commitsById,
                                              List<NamedCommit> commitsByNameAndId, String commitMessage,
                                              MergeCommand.FastForwardMode fastForwardMode,
                                              ProgressMonitor monitor) throws GitAPIException {
        MergeCommand merge = git.merge();

        setupMergeCommand(merge, commitsByRef, commitsById, commitsByNameAndId, monitor, strategy, fastForwardMode);

        return git.merge()
                .setMessage(commitMessage)           // message to be used for merge commit
                .call();
    }

//    public static void rebase(Git git) throws GitAPIException {
//        git.rebase()
//                .setStrategy()
//                .setProgressMonitor()
//                .setOperation()
//                .setPreserveMerges()
//                .setUpstream()
//                .setUpstreamName()
//                .runInteractively(RebaseCommand.InteractiveHandler)
//                .tryFastForward()
//                .carry()
//                .call();
//    }

    public static void cherrypick(Git git) throws GitAPIException {
//        git.cherryPick()
//                .setMainlineParentNumber()
//                .setStrategy()
//                .setNoCommit()
//                .setReflogPrefix()
//                .setOurCommitName()
//                .include()
//                .call();

    }

    /* ************************* *
     * Reverting & Resetting
     * ************************* */

    /**
     * Reverts the given modified files back to their previous state in the most recent commit. Note: this does not
     * create a commit that reverts a previous commit.
     * @param git the git repository
     * @param relativePaths the list of files' paths relative to the parent directory (given "/home/user/dir/.git/",
     *                      the parent directory would be "/home/user/dir/")
     * @throws GitAPIException
     */
    public static void revertChanges(Git git, List<String> relativePaths) throws GitAPIException {
        CheckoutCommand checkout = git.checkout();
        relativePaths.forEach(checkout::addPath);
        checkout.call();
    }

    /**
     * Wrapper object for use in {@link #revertCommits(Git, List, List, List)}'s {@code commitsByNameAndId}
     */
    public static class NamedCommit {
        private final String name;
        public final String getName() {return name; }
        
        private final AnyObjectId objectId;
        public final AnyObjectId getObjectId() {return objectId; }
        
        public NamedCommit(String name, AnyObjectId objectId) {
            this.name = name;
            this.objectId = objectId;
        }
    }

    /**
     * Creates a new commit that reverts the changes done in the previous (erroneous) commit(s).
     * @param git the git repository
     * @param commitsByRef one or more commits (can be null if at least one other "commitsBy*" is not null)
     * @param commitsById one or more commits (can be null if at least one other "commitsBy*" is not null)
     * @param commitsByNameAndId one or more commits (can be null if at least one other "commitsBy*" is not null)
     * @return the commit made that reverts previous commits' work
     * @throws GitAPIException
     */
    public static RevCommit revertCommits(Git git, List<Ref> commitsByRef, List<AnyObjectId> commitsById,
                                     List<NamedCommit> commitsByNameAndId) throws GitAPIException {
        return revertCommits(git, commitsByRef, commitsById, commitsByNameAndId, MergeStrategy.RESOLVE);
    }

    /**
     * Creates a new commit that reverts the changes done in the previous (erroneous) commit(s)
     * @param git the git repository
     * @param commitsByRef one or more commits (can be null if at least one other "commitsBy*" is not null)
     * @param commitsById one or more commits (can be null if at least one other "commitsBy*" is not null)
     * @param commitsByNameAndId one or more commits (can be null if at least one other "commitsBy*" is not null)
     * @param strategy the merge strategy to use
     * @return the commit made that reverts previous commits' work
     * @throws GitAPIException
     */
    public static RevCommit revertCommits(Git git, List<Ref> commitsByRef, List<AnyObjectId> commitsById,
                                     List<NamedCommit> commitsByNameAndId, MergeStrategy strategy) throws GitAPIException {
        return revertCommits(git, commitsByRef, commitsById, commitsByNameAndId, strategy, "OURS");
    }

    /**
     * Creates a new commit that reverts the changes done in the previous (erroneous) commit(s)
     * @param git the git repository
     * @param commitsByRef one or more commits (can be null if at least one other "commitsBy*" is not null)
     * @param commitsById one or more commits (can be null if at least one other "commitsBy*" is not null)
     * @param commitsByNameAndId one or more commits (can be null if at least one other "commitsBy*" is not null)
     * @param strategy the merge strategy to use
     * @param ourCommitName named used for the "OURS" place when there are conflicts 
     *                      (distinguishes our commits from others' commits)
     * @return the commit made that reverts previous commits' work
     * @throws GitAPIException
     */
    public static RevCommit revertCommits(Git git, List<Ref> commitsByRef, List<AnyObjectId> commitsById,
                                     List<NamedCommit> commitsByNameAndId, MergeStrategy strategy, 
                                     String ourCommitName) throws GitAPIException {
        RevertCommand revert = git.revert();
        
        commitsByRef.forEach(revert::include);
        commitsById.forEach(revert::include);
        commitsByNameAndId.forEach(nc -> revert.include(nc.getName(), nc.getObjectId()));
        
        return revert
                .setStrategy(strategy)
                .setOurCommitName(ourCommitName)
                .call();
    }

    /**
     * Resets the repository to HEAD
     * @param git the git repository
     * @param mode the reset type to use
     * @throws GitAPIException
     */
    public static void reset(Git git, ResetCommand.ResetType mode) throws GitAPIException {
        git.reset()
                .setMode(mode)
                .call();
    }

    /**
     * Resets the repository to the given ref
     * @param git the git repository
     * @param mode the reset type to use
     * @param ref the ref to which to reset the given files
     * @throws GitAPIException
     */
    public static void reset(Git git, ResetCommand.ResetType mode, String ref) throws GitAPIException {
        git.reset()
                .setMode(mode)
                .setRef(ref)
                .call();
    }

    /**
     * Resets the given files to HEAD
     * @param git the git repository
     * @param mode the reset type to use
     * @param relativePaths the repository-relative file path of file/directory to reset (with / as separator)
     * @throws GitAPIException
     */
    public static void reset(Git git, ResetCommand.ResetType mode, List<String> relativePaths) throws GitAPIException {
        ResetCommand reset = git.reset();
        relativePaths.forEach(reset::addPath);
        reset.setMode(mode).call();
    }

    /**
     * Resets the given files to the given ref
     * @param git the git repository
     * @param mode the reset type to use
     * @param relativePaths the repository-relative file path of file/directory to reset (with / as separator)
     * @param ref the ref to which to reset the given files
     * @throws GitAPIException
     */
    public static void reset(Git git, ResetCommand.ResetType mode, List<String> relativePaths,
                             String ref) throws GitAPIException {
        ResetCommand reset = git.reset();
        relativePaths.forEach(reset::addPath);
        reset
                .setMode(mode)
                .setRef(ref)
                .call();
    }

    /* ************************* *
     * Branch-related
     * ************************* */

    /**
     * Checks out a branch by its given name
     * @param git the git repository
     * @param branchName the name of the branch to check out.
     * @return the checked out branch
     * @throws GitAPIException
     */
    public static Ref checkoutBranch(Git git, String branchName) throws GitAPIException {
        return git.checkout().setName(branchName).call();
    }

    /**
     * Creates a new branch where {@code HEAD} is and check its out
     * @param git the git repository
     * @param branchName name of the new branch
     * @return the checked out branch
     * @throws GitAPIException
     */
    public static Ref checkoutNewLocalBranch(Git git, String branchName) throws GitAPIException {
        return git.checkout()
                .setCreateBranch(true)
                .setName(branchName)
                // "start point" defaults to HEAD if not specified, so no need to specify it here
                .call();
    }

    /**
     * Creates a new branch at the given start point and checks it out
     * @param git the git repository
     * @param branchName the name of the new branch
     * @param startPoint the starting point of the branch
     * @return the checked out branch
     * @throws GitAPIException
     */
    public static Ref checkoutNewLocalBranch(Git git, String branchName, String startPoint) throws GitAPIException {
        return git.checkout()
                .setCreateBranch(true)
                .setName(branchName)
                .setStartPoint(startPoint)
                .call();
    }

    /**
     * Creates a new branch at the given start point and checks it out
     * @param git the git repository
     * @param branchName the name of the new branch
     * @param startPoint the starting point of the branch
     * @return the checked out branch
     * @throws GitAPIException
     */
    public static Ref checkoutNewLocalBranch(Git git, String branchName, RevCommit startPoint) throws GitAPIException {
        return git.checkout()
                .setCreateBranch(true)
                .setName(branchName)
                .setStartPoint(startPoint.getName())
                .call();
    }

    /**
     * Creates a new branch at the given start point and tracks the remote branch. Then, checks out the new branch.
     * @param git the git repository
     * @param branchName the name of the new branch
     * @param startPoint the starting point of the new branch
     * @return the checked out branch
     * @throws GitAPIException
     */
    public static Ref checkoutNewBranchFromRemote(
            Git git, String branchName, String startPoint) throws GitAPIException {
        return checkoutNewBranchFromRemote(git, branchName, startPoint, CreateBranchCommand.SetupUpstreamMode.TRACK);
    }

    /**
     * Creates a new branch at the given start point and checks it out.
     * @param git the git repository
     * @param branchName the name of the new branch
     * @param startPoint the starting point of the new branch
     * @param upstreamMode whether to track the upstream branch or not.
     * @return the checked out branch
     * @throws GitAPIException
     */
    public static Ref checkoutNewBranchFromRemote(
            Git git, String branchName, String startPoint,
            CreateBranchCommand.SetupUpstreamMode upstreamMode) throws GitAPIException {
        return git.checkout()
                .setCreateBranch(true)
                .setName(branchName)
                .setStartPoint(startPoint)
                .setUpstreamMode(upstreamMode)
                .call();
    }

    /**
     * Creates a new branch at the given start point and tracks the remote branch. Then, checks out the new branch.
     * @param git the git repository
     * @param branchName the name of the new branch
     * @param startPoint the starting point of the new branch
     * @return the checked out branch
     * @throws GitAPIException
     */
    public static Ref checkoutNewBranchFromRemote(
            Git git, String branchName, RevCommit startPoint) throws GitAPIException {
        return checkoutNewBranchFromRemote(git, branchName, startPoint, CreateBranchCommand.SetupUpstreamMode.TRACK);
    }

    /**
     * Creates a new branch at the given start point and checks it out.
     * @param git the git repository
     * @param branchName the name of the new branch
     * @param startPoint the starting point of the new branch
     * @param upstreamMode whether to track the upstream branch or not.
     * @return the checked out branch
     * @throws GitAPIException
     */
    public static Ref checkoutNewBranchFromRemote(
            Git git, String branchName, RevCommit startPoint,
            CreateBranchCommand.SetupUpstreamMode upstreamMode) throws GitAPIException {
        return git.checkout()
                .setCreateBranch(true)
                .setName(branchName)
                .setStartPoint(startPoint)
                .setUpstreamMode(upstreamMode)
                .call();
    }

    public static Ref createNewLocalBranch(
            Git git, String branchName, String startingPoint) throws GitAPIException {
        return git.branchCreate()
                .setStartPoint(startingPoint)
                .setName(branchName)
                .call();
    }

    public static Ref createNewLocalBranch(
            Git git, String branchName, RevCommit startingPoint) throws GitAPIException {
        return git.branchCreate()
                .setStartPoint(startingPoint)
                .setName(branchName)
                .call();
    }

    public static Ref createNewBranchFromRemote(
            Git git, String branchName, RevCommit startingPoint,
            CreateBranchCommand.SetupUpstreamMode upstreamMode) throws GitAPIException {
        return git.branchCreate()
                .setStartPoint(startingPoint)
                .setName(branchName)
                .setUpstreamMode(upstreamMode)
                .call();
    }

    public static Ref createNewBranchFromRemote(
            Git git, String branchName, String startingPoint,
            CreateBranchCommand.SetupUpstreamMode upstreamMode) throws GitAPIException {
        return git.branchCreate()
                .setStartPoint(startingPoint)
                .setName(branchName)
                .setUpstreamMode(upstreamMode)
                .call();
    }

    /**
     * Deletes the given branches.
     * @param git the git repository
     * @param branchNames the names of the branches. Invalid names will be ignored.
     * @throws GitAPIException
     */
    public static void deleteBranches(Git git, String... branchNames) throws GitAPIException {
        git.branchDelete().setBranchNames(branchNames).call();
    }

    /**
     * Renames the currently checked-out branch to the given new name
     * @param git the git repository
     * @param newBranchName the new branch name
     * @throws GitAPIException
     */
    public static void renameCurrentBranchTo(Git git, String newBranchName) throws GitAPIException {
        git.branchRename().setNewName(newBranchName).call();
    }

    /**
     * Renames the given branch to the new name
     * @param git the git repository
     * @param branchToRename the branch to rename
     * @param newBranchName the new name to use
     * @throws GitAPIException
     */
    public static void renameBranchTo(Git git, String branchToRename, String newBranchName) throws GitAPIException {
        git.branchRename()
                .setOldName(branchToRename)
                .setNewName(newBranchName)
                .call();
    }

    /**
     * Indicates which branch type (local, remote, both) to use when getting branches
     */
    public enum BranchType {
        /** Only include the local branches */
        LOCAL,
        /** Only include the remote branches */
        REMOTE,
        /** Include both local and remote branches */
        BOTH
    }

    /**
     * Determines whether to return local, remote, or all branches for {@code getBranches*()} related methods.
     * @param branchList the branch list command
     * @param branchType the type of branches to include
     */
    private static void setBranchType(ListBranchCommand branchList, BranchType branchType) {
        if (branchType.equals(BranchType.LOCAL)) {
            // to get local branches, don't set list mode
            return;
        }

        if (branchType.equals(BranchType.REMOTE)) {
            branchList.setListMode(ListBranchCommand.ListMode.REMOTE);
        } else {
            branchList.setListMode(ListBranchCommand.ListMode.ALL);
        }
    }

    public static List<Ref> getBranches(Git git, BranchType branchType) throws GitAPIException {
        ListBranchCommand branchList = git.branchList();
        setBranchType(branchList, branchType);
        return branchList.call();
    }

    /**
     * Gets the list of branches that contain the given commit
     * @param git the git repository
     * @param commit a commit ID or ref name
     * @return the list of branches with the given commit
     * @throws GitAPIException
     */
    public static List<Ref> getBranchesWithCommit(Git git, BranchType branchType, String commit) throws GitAPIException {
        ListBranchCommand branchList = git.branchList();
        setBranchType(branchList, branchType);
        branchList.setContains(commit);
        return branchList.call();
    }

    /* ************************* *
     * Remote-related
     * ************************* */

    /**
     * Fetches all content from remote "origin". Note: this command will also prune deleted references
     * @param git the git repository
     * @return result of the fetch
     * @throws GitAPIException
     */
    public static FetchResult fetch(Git git) throws GitAPIException {
        return fetch(git, Constants.DEFAULT_REMOTE_NAME);
    }

    /**
     * Fetches all content from the given remote. Note: this command will also prune deleted references
     * @param git the git repository
     * @param remoteName the name of the remote or the uri of the remote
     * @return result of the fetch
     * @throws GitAPIException
     */
    public static FetchResult fetch(Git git, String remoteName) throws GitAPIException {
        return git.fetch()
                .setRemote(remoteName)
                .setCheckFetchedObjects(true)
                .setRemoveDeletedRefs(true)
                .call();
    }

    /**
     * Selectively fetches content from the given remote name. Note: this command will also prune deleted references
     * @param git the git repository
     * @param remoteName the name of the remote or the uri of the remote
     * @param refSpecs the content to fetch:
     * @return result of the fetch
     * @throws GitAPIException
     */
    public static FetchResult fetch(Git git, String remoteName, List<RefSpec> refSpecs) throws GitAPIException {
        return fetch(git, remoteName, refSpecs, null);
    }

    /**
     * Selectively fetches content from the given remote name, including which tags (if any) to include.
     * Note: this command will also prune deleted references
     * @param git the git repository
     * @param remoteName the name of the remote or the uri of the remote
     * @param refSpecs the content to fetch:
     * @param tagOpt whether to also fetch no tags, the relevant tags, or all tags.
     * @return result of the fetch
     * @throws GitAPIException
     */
    public static FetchResult fetch(Git git, String remoteName, List<RefSpec> refSpecs, TagOpt tagOpt) throws GitAPIException {
        if (tagOpt == null) { tagOpt = TagOpt.AUTO_FOLLOW; }
        return fetch(git, remoteName, refSpecs, tagOpt, null);
    }

    /**
     * Selectively fetches content from the given remote name, including which tags (if any) to include and a monitor
     * that reports the progress. Note: this command will also prune deleted references
     * @param git the git repository
     * @param remoteName the name of the remote or the uri of the remote
     * @param refSpecs the content to fetch:
     * @param tagOpt whether to also fetch no tags, the relevant tags, or all tags.
     * @param monitor reports the progress of the fetch process
     * @return result of the fetch
     * @throws GitAPIException
     */
    public static FetchResult fetch(Git git, String remoteName, List<RefSpec> refSpecs, TagOpt tagOpt, ProgressMonitor monitor) throws GitAPIException {
        FetchCommand fetch = git.fetch();

        if (monitor != null) { fetch.setProgressMonitor(monitor); }
        return fetch
                .setRemote(remoteName)
                .setRefSpecs(refSpecs)
                .setTagOpt(tagOpt)
                .setRemoveDeletedRefs(true)
                .setCheckFetchedObjects(true)
                .call();
    }

    /**
     * Using the currently checked-out branch' configuration, pulls from the repository and merges changes from
     * tracked branch into the local checked-out branch using the {@link MergeStrategy#RESOLVE} merge strategy.
     * @param git the git repository
     * @return result of the pull
     * @throws GitAPIException
     */
    public static PullResult pullWithMerge(Git git) throws GitAPIException {
        return pullWithMerge(git, MergeStrategy.RESOLVE);
    }

    /**
     * Pulls from the "origin" repository and merges changes from the tracked branch into
     * the local checked-out branch using the given strategy
     * @param git the git repository
     * @param strategy the merge strategy:
     * @return result of the pull
     * @throws GitAPIException
     */
    public static PullResult pullWithMerge(Git git, MergeStrategy strategy) throws GitAPIException {
        return pullWithMerge(git, strategy, null);
    }

    /**
     * Pulls from the given repository and merges changes from the tracked branch into
     * the local checked-out branch using the given strategy
     * @param git the git repository
     * @param strategy the merge strategy:
     * @param remoteName the name of the repository
     * @return result of the pull
     * @throws GitAPIException
     */
    public static PullResult pullWithMerge(Git git, MergeStrategy strategy, String remoteName) throws GitAPIException {
        return pullWithMerge(git, strategy, remoteName, null);
    }

    /**
     * Pulls from the given repository and merges changes from the given remote branch into
     * the local checked-out branch using the given strategy
     * @param git the git repository
     * @param strategy the merge strategy:
     * @param remoteName the name of the repository
     * @param branchName the name of the remote branch
     * @return result of the pull
     * @throws GitAPIException
     */
    public static PullResult pullWithMerge(Git git, MergeStrategy strategy,
                                     String remoteName, String branchName) throws GitAPIException {
        return pullWithMerge(git, strategy, remoteName, branchName, null);
    }

    /**
     * Pulls from the given repository and merges changes from the given remote branch into
     * the local checked-out branch using the given strategy. Progress is reported via the {@code monitor}.
     * @param git the git repository
     * @param strategy the merge strategy:
     * @param remoteName the name of the repository
     * @param branchName the name of the remote branch
     * @param monitor reports the progress of the pull
     * @return result of the pull
     * @throws GitAPIException
     */
    public static PullResult pullWithMerge(Git git, MergeStrategy strategy, String remoteName, String branchName,
                                           ProgressMonitor monitor) throws GitAPIException {
        PullCommand pull = git.pull();

        if (monitor != null) { pull.setProgressMonitor(monitor); }

        return pull
                .setStrategy(strategy)
                .setRemote(remoteName)            // value -> current branch config -> DEFAULT_REMOTE_NAME = "origin"
                .setRemoteBranchName(branchName)  // value -> current branch config -> current branch name
                .call();
    }

    /**
     * Using the currently checked-out branch' configuration, pulls from the "origin" repository and
     * rebases the currently checked-out branch onto the tracked branch.
     * @param git the git repository
     * @return result of the pull
     * @throws GitAPIException
     */
    public static PullResult pullWithRebase(Git git) throws GitAPIException {
        return pullWithRebase(git, null);
    }

    /**
     * Pulls from the given repository and rebases the currently checked-out branch onto the tracked branch.
     * @param git the git repository
     * @param remoteName the name of the remote repository
     * @return result of the pull
     * @throws GitAPIException
     */
    public static PullResult pullWithRebase(Git git, String remoteName) throws GitAPIException {
        return pullWithRebase(git, remoteName, null);
    }

    /**
     * Pulls from the given repository and rebases the currently checked-out branch onto the given remote branch.
     * @param git the git repository
     * @param remoteName the name of the remote repository
     * @param branchName the name of the branch on which to rebase the current branch
     * @return result of the pull
     * @throws GitAPIException
     */
    public static PullResult pullWithRebase(Git git, String remoteName, String branchName) throws GitAPIException {
        return pullWithRebase(git, remoteName, branchName, null);
    }

    /**
     * Pulls from the given repository and rebases the currently checked-out branch onto the given remote branch
     * and includes a report on the progress of the pull.
     * @param git the git repository
     * @param remoteName the name of the remote repository
     * @param branchName the name of the branch on which to rebase the current branch
     * @param monitor reports the progress of the pull
     * @return result of the pull
     * @throws GitAPIException
     */
    public static PullResult pullWithRebase(Git git, String remoteName, String branchName,
                                      ProgressMonitor monitor) throws GitAPIException {
        PullCommand pull = git.pull();

        if (monitor != null) { pull.setProgressMonitor(monitor); }

        return pull
                .setRebase(true)                 // when true, ignores merge strategy
                .setRemote(remoteName)           // value -> current branch config -> DEFAULT_REMOTE_NAME = "origin"
                .setRemoteBranchName(branchName) // value -> current branch config -> current branch name
                .setProgressMonitor(monitor)
                .call();
    }

    /**
     * Pushes all branches and tags to the "origin" repository
     * @param git the git repository
     * @return the results of the push
     * @throws GitAPIException
     */
    public static Iterable<PushResult> pushAll(Git git) throws GitAPIException {
        return pushAll(git, Constants.DEFAULT_REMOTE_NAME);
    }

    /**
     * Pushes all branches and tags to the given remote repository
     * @param git the git repository
     * @param remoteName the name of the remote repository
     * @return the results of the push
     * @throws GitAPIException
     */
    public static Iterable<PushResult> pushAll(Git git, String remoteName) throws GitAPIException {
        return git.push()
                .setRemote(remoteName)
                .setPushAll()
                .setPushTags()
                .call();
    }

    public static Iterable<PushResult> push(Git git) throws GitAPIException {
        return push(git, Constants.DEFAULT_REMOTE_NAME);
    }

    public static Iterable<PushResult> push(Git git, String remoteName) throws GitAPIException {
        return git.push().setRemote(remoteName).call();
    }

    /**
     * Pushes the refspecs to the given remote repository using a non-atomic push.
     * @param git the git repository
     * @param remoteName the name of the remote repository
     * @param refSpecs the refspecs to use
     * @return the results of the push
     * @throws GitAPIException
     */
    public static Iterable<PushResult> push(Git git, String remoteName, List<RefSpec> refSpecs) throws GitAPIException {
        return push(git, remoteName, refSpecs, false, null);
    }

    /**
     * Pushes to the remote repository
     * @param git the git repository
     * @param remoteName the name of the remote
     * @param refSpecs the refspecs to use
     * @param useAtomicPush if true, everything will be pushed or nothing at all if errors occur. Will fail if remote
     *                      repository does not support atomic. Most push calls will set this to {@code false}.
     * @param monitor reports the progress of push
     * @return the results of the push
     * @throws GitAPIException
     */
    public static Iterable<PushResult> push(Git git, String remoteName, List<RefSpec> refSpecs, boolean useAtomicPush,
                                  ProgressMonitor monitor) throws GitAPIException {
        PushCommand push = git.push();

        if (monitor != null) { push.setProgressMonitor(monitor); }

        return push
                .setRemote(remoteName)      // value -> "origin" (DEFAULT_REMOTE_NAME)
                .setRefSpecs(refSpecs)
                .setAtomic(useAtomicPush)
                .call();
    }

}
