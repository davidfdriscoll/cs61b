package gitlet;

import java.io.File;
import java.nio.file.FileSystems;
import java.util.*;

import static gitlet.Utils.*;

/** Represents a gitlet repository.
 *
 *  @author David Driscoll
 */
public class Repository {
    /** The current working directory. */
    public static final File USER_DIR = new File(System.getProperty("user.dir"));
    public static final File CWD = USER_DIR;
    /** The .gitlet directory. */
    public static final File GITLET_DIR = join(CWD, ".gitlet");

    public static File getObjectsDir(File gitletDir) {
        return Utils.join(gitletDir, "objects");
    }
    public static File getFoldersDir(File gitletDir) {
        return join(getObjectsDir(gitletDir), "folders");
    }
    public static File getFileBlobsDir(File gitletDir) {
        return join(getObjectsDir(gitletDir), "files");
    }
    public static File getCommitsDir(File gitletDir) {
        return join(getObjectsDir(gitletDir), "commits");
    }
    public static File getRefsDir(File gitletDir) {
        return Utils.join(gitletDir, "refs");
    }
    public static File getHeadsDir(File gitletDir) {
        return join(getRefsDir(gitletDir), "heads");
    }
    public static File getRemotesDir(File gitletDir) {
        return join(getRefsDir(gitletDir), "remotes");
    }

    public static void init() {
        if (GITLET_DIR.exists()) {
            System.out.println(
                "A Gitlet version-control system already exists in the current directory."
            );
            return;
        }

        GITLET_DIR.mkdir();
        getObjectsDir(GITLET_DIR).mkdir();
        getCommitsDir(GITLET_DIR).mkdir();
        getFileBlobsDir(GITLET_DIR).mkdir();
        getFoldersDir(GITLET_DIR).mkdir();
        getRefsDir(GITLET_DIR).mkdir();
        getHeadsDir(GITLET_DIR).mkdir();
        getRemotesDir(GITLET_DIR).mkdir();

        Folder emptyFolder = Folder.emptyFolder();
        String emptyFolderSha = emptyFolder.generateSha();
        emptyFolder.saveToSha(emptyFolderSha);

        StagingArea emptyStagingArea = new StagingArea();
        emptyStagingArea.save();

        Commit initialCommit = Commit.initialCommit();
        initialCommit.save();

        String branchName = "master";
        Branch master = new Branch(branchName, initialCommit.getSha());
        master.save();

        Head.setBranchName(branchName);
    }

    public static void add(String filename) {
        FileBlob blob = FileBlob.fromFilename(filename);
        if (blob == null) {
            System.out.println("File does not exist.");
            return;
        }
        blob.save();

        Folder currentFolder = Folder.fromHead();

        StagingArea stagingArea = StagingArea.fromFile();
        stagingArea.addFile(filename, blob, currentFolder);
        stagingArea.save();
    }

    public static void rm(String filename) {
        Folder currentFolder = Folder.fromHead();

        if (currentFolder.containsFile(filename)) {
            File workingDirectoryFile = Utils.join(CWD, filename);
            Utils.restrictedDelete(workingDirectoryFile);
        }

        StagingArea stagingArea = StagingArea.fromFile();
        stagingArea.removeFile(filename, currentFolder);
        stagingArea.save();
    }

    public static void commit(String message) {
        String branchName = Head.getBranchName();
        Branch branch = Branch.fromBranchName(branchName);
        assert branch != null;
        String currentCommitSha = branch.getCommitSha();
        Commit currentCommit = Commit.fromSha(currentCommitSha);
        assert currentCommit != null;
        String currentFolderSha = currentCommit.getFolderSha();
        Folder currentFolder = Folder.fromSha(currentFolderSha);

        StagingArea stagingArea = StagingArea.fromFile();
        Folder newFolder = stagingArea.updateFolder(currentFolder);
        String newFolderSha = newFolder.generateSha();
        if (Objects.equals(newFolderSha, currentFolderSha)) {
            System.out.println("No changes added to the commit.");
            return;
        }
        newFolder.saveToSha(newFolderSha);

        Long timestamp = new Date().getTime();
        Commit newCommit = new Commit(
            message, newFolderSha, currentCommitSha, "-1", timestamp
        );
        newCommit.save();

        stagingArea.clear();
        stagingArea.save();

        branch.setCommitSha(newCommit.getSha());
        branch.save();
    }

    public static void log() {
        String branchName = Head.getBranchName();
        Branch branch = Branch.fromBranchName(branchName);
        assert branch != null;
        String commitSha = branch.getCommitSha();

        while (!Objects.equals(commitSha, "-1")) {
            Commit commit = Commit.fromSha(commitSha);
            commit.print();
            commitSha = commit.getParentSha();
        }
    }

    public static void globalLog() {
        List<String> commitShas = Commit.findAllCommitShas();

        for (String commitSha: commitShas) {
            Commit commit = Commit.fromSha(commitSha);
            assert commit != null;
            commit.print();
        }
    }

    public static void find(String commitMessage) {
        List<String> commitShas = Commit.findAllCommitShas();
        boolean hasFoundCommit = false;

        for (String commitSha: commitShas) {
            Commit commit = Commit.fromSha(commitSha);
            assert commit != null;
            if (Objects.equals(commit.getMessage(), commitMessage)) {
                System.out.println(commitSha);
                hasFoundCommit = true;
            }
        }
        if (!hasFoundCommit) {
            System.out.println("Found no commit with that message.");
        }
    }

    public static void checkoutFileFromHead(String filename) {
        String branchName = Head.getBranchName();
        Branch branch = Branch.fromBranchName(branchName);
        assert branch != null;
        checkoutFileFromCommit(branch.getCommitSha(), filename);
    }

    public static void checkoutFileFromCommit(String commitSha, String filename) {
        Commit commit = Commit.fromSha(commitSha);
        if (commit == null) {
            return;
        }

        String folderSha = commit.getFolderSha();
        Folder folder = Folder.fromSha(folderSha);
        if (!folder.containsFile(filename)) {
            System.out.println("File does not exist in that commit");
            return;
        }
        String fileBlobSha = folder.getFileBlobSha(filename);
        FileBlob fileblob = FileBlob.fromSha(fileBlobSha);
        assert fileblob != null;
        fileblob.writeToFile(filename);
    }

    public static void status() {
        List<String> branchNames = Utils.plainFilenamesIn(getHeadsDir(GITLET_DIR));
        assert branchNames != null;
        Collections.sort(branchNames);
        String currentBranchName = Head.getBranchName();

        System.out.println("=== Branches ===");
        for (String branchName: branchNames) {
            if (Objects.equals(branchName, currentBranchName)) {
                System.out.print("*");
            }
            System.out.println(branchName);
        }

        System.out.println();
        StagingArea stagingArea = StagingArea.fromFile();
        stagingArea.print();

        WorkingDirectory workingDirectory = new WorkingDirectory();

        System.out.println("=== Modifications Not Staged For Commit ===");
        List<String> modifiedFiles = workingDirectory.getModificationsNotStagedForCommit();
        for (String file: modifiedFiles) {
            System.out.println(file);
        }
        System.out.println();

        System.out.println("=== Untracked Files ===");
        List<String> untrackedFiles = workingDirectory.getUntrackedFiles();
        for (String file: untrackedFiles) {
            System.out.println(file);
        }
        System.out.println();
    }

    public static void branch(String branchName) {
        Branch existingBranch = Branch.fromBranchName(branchName);
        if (existingBranch != null) {
            System.out.println("A branch with that name already exists.");
            return;
        }

        String headBranchName = Head.getBranchName();
        Branch headBranch = Branch.fromBranchName(headBranchName);
        assert headBranch != null;
        String headBranchSha = headBranch.getCommitSha();
        Branch newBranch = new Branch(branchName, headBranchSha);
        newBranch.save();
    }

    public static void removeBranch(String branchName) {
        String headBranchName = Head.getBranchName();
        if (Objects.equals(branchName, headBranchName)) {
            System.out.println("Cannot remove the current branch.");
            return;
        }

        Branch branch = Branch.fromBranchName(branchName);
        if (branch == null) {
            System.out.println("A branch with that name does not exist.");
            return;
        }
        branch.delete();
    }

    public static void addRemote(String remoteName, String remotePath) {
        Remote existingRemote = Remote.fromRemoteName(remoteName);
        if (existingRemote != null) {
            System.out.println("A remote with that name already exists.");
            throw new RuntimeException();
        }

        Remote newRemote = new Remote(remoteName, remotePath);
        newRemote.save();

        File headsDir = join(getHeadsDir(GITLET_DIR), remoteName);
        if (!headsDir.exists()) {
            headsDir.mkdir();
        }
    }

    public static void removeRemote(String remoteName) {
        Remote remote = Remote.fromRemoteName(remoteName);
        if (remote == null) {
            System.out.println("A remote with that name does not exist.");
            throw new RuntimeException();
        }
        remote.delete();
    }

    public static void checkoutBranch(String branchName) {
        Branch.checkout(branchName);
    }

    public static void reset(String commitSha) {
        new WorkingDirectory().reset(commitSha);

        Branch branch = Branch.fromBranchName(Head.getBranchName());
        assert branch != null;
        branch.setCommitSha(commitSha);
        branch.save();
    }

    public static void merge(String givenBranchName) {
        StagingArea stagingArea = StagingArea.fromFile();

        if (!stagingArea.isEmpty()) {
            System.out.println("You have uncommitted changes.");
            return;
        }

        Branch mergeBranch = Branch.fromBranchName(givenBranchName);
        if (mergeBranch == null) {
            System.out.println("A branch with that name does not exist.");
            return;
        }
        String currentBranchName = Head.getBranchName();
        Branch currentBranch = Branch.fromBranchName(currentBranchName);
        assert currentBranch != null;
        currentBranch.merge(mergeBranch);
    }

    private static String createLocalNameForRemote(String remoteName, String remoteBranchName) {
        return remoteName + FileSystems.getDefault().getSeparator() + remoteBranchName;
    }

    public static void fetch(String remoteName, String remoteBranchName) {
        Remote remote = Remote.fromRemoteName(remoteName);
        assert remote != null;
        if (!remote.getRemotePath().exists()) {
            System.out.println("Remote directory not found.");
            throw new RuntimeException();
        }
        Branch remoteBranch = Branch.fromRemote(remote, remoteBranchName);
        if (remoteBranch == null) {
            System.out.println("That remote does not have that branch.");
            throw new RuntimeException();
        }
        String remoteCommitSha = remoteBranch.getCommitSha();

        String localName = createLocalNameForRemote(remoteName, remoteBranchName);
        Branch localBranch = Branch.fromBranchName(localName);
        if (localBranch == null) {
            localBranch = new Branch(localName, remoteCommitSha);
        } else {
            localBranch.setCommitSha(remoteCommitSha);
        }
        localBranch.save();

        Commit.copyToRepository(remoteCommitSha, GITLET_DIR, remote.getRemotePath());
    }

    public static void push(String remoteName, String remoteBranchName) {
        Remote remote = Remote.fromRemoteName(remoteName);
        assert remote != null;
        if (!remote.getRemotePath().exists()) {
            System.out.println("Remote directory not found.");
            throw new RuntimeException();
        }
        Branch remoteBranch = Branch.fromRemote(remote, remoteBranchName);
        String remoteCommitSha = remoteBranch.getCommitSha();

        String localBranchName = Head.getBranchName();
        Branch localBranch = Branch.fromBranchName(localBranchName);
        String localCommitSha = localBranch.getCommitSha();
        Commit localCommit = Commit.fromSha(localCommitSha);

        Set<String> ancestry = Commit.getAncestry(GITLET_DIR, localCommit);
        if (!ancestry.contains(remoteCommitSha)) {
            System.out.println("Please pull down remote changes before pushing.");
            throw new RuntimeException();
        }

        Commit.copyToRepository(localCommitSha, remote.getRemotePath(), GITLET_DIR);
        remoteBranch.setCommitSha(localCommitSha);
        remoteBranch.save(remote.getRemotePath());
    }

    public static void pull(String remoteName, String remoteBranchName) {
        fetch(remoteName, remoteBranchName);
        String localName = createLocalNameForRemote(remoteName, remoteBranchName);
        merge(localName);
    }
}
