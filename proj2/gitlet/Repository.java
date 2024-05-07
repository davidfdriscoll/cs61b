package gitlet;

import java.io.File;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import static gitlet.Branch.HEADS_FOLDER;
import static gitlet.Commit.COMMITS_FOLDER;
import static gitlet.FileBlob.FILEBLOBS_FOLDER;
import static gitlet.Folder.FOLDERS_FOLDER;
import static gitlet.Utils.*;

/** Represents a gitlet repository.
 *
 *  @author David Driscoll
 */
public class Repository {

    private static final Boolean useTestDirectory = false;

    /** The current working directory. */
    public static final File USER_DIR = new File(System.getProperty("user.dir"));
    public static final File CWD = useTestDirectory ? join(USER_DIR, "mytest") : USER_DIR;
    /** The .gitlet directory. */
    public static final File GITLET_DIR = join(CWD, ".gitlet");
    /** objects folder within .gitlet directory */
    public static File OBJECTS_FOLDER = Utils.join(GITLET_DIR, "objects");
    public static File REFS_FOLDER = Utils.join(GITLET_DIR, "refs");

    public static void init() {
        if (GITLET_DIR.exists()) {
            System.out.println("A Gitlet version-control system already exists in the current directory.");
            return;
        }

        GITLET_DIR.mkdir();
        OBJECTS_FOLDER.mkdir();
        COMMITS_FOLDER.mkdir();
        FILEBLOBS_FOLDER.mkdir();
        FOLDERS_FOLDER.mkdir();
        REFS_FOLDER.mkdir();
        HEADS_FOLDER.mkdir();

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

        StagingArea stagingArea = StagingArea.fromFile();
        stagingArea.removeFile(filename, currentFolder);
        stagingArea.save();
    }

    public static void commit(String message) {
        String branchName = Head.getBranchName();
        Branch branch = Branch.fromBranchName(branchName);
        String currentCommitSha = branch.getCommitSha();
        Commit currentCommit = Commit.fromSha(currentCommitSha);
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
        String commitSha = branch.getCommitSha();

        while (!Objects.equals(commitSha, "-1")) {
            Commit commit = Commit.fromSha(commitSha);
            commit.print();
            commitSha = commit.getParentSha();
        }
    }

    public static void globalLog() {
        List<String> commitShas = Utils.plainFilenamesIn(COMMITS_FOLDER);

        assert commitShas != null;
        for (String commitSha: commitShas) {
            Commit commit = Commit.fromSha(commitSha);
            commit.print();
        }
    }

    public static void find(String commitMessage) {
        List<String> commitShas = Utils.plainFilenamesIn(COMMITS_FOLDER);

        assert commitShas != null;
        for (String commitSha: commitShas) {
            Commit commit = Commit.fromSha(commitSha);
            if (Objects.equals(commit.getMessage(), commitMessage)) {
                System.out.println(commitSha);
            }
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
        String folderSha = commit.getFolderSha();
        Folder folder = Folder.fromSha(folderSha);
        if (!folder.containsFile(filename)) {
            System.out.println("File does not exist in that commit");
            return;
        }
        String fileBlobSha = folder.getFileBlobSha(filename);
        FileBlob fileblob = FileBlob.fromSha(fileBlobSha);
        fileblob.writeToFile(filename);
    }

    public static void status() {
        List<String> branchNames = Utils.plainFilenamesIn(HEADS_FOLDER);
        String currentBranchName = Head.getBranchName();

        System.out.println("=== Branches ===");
        assert branchNames != null;
        for (String branchName: branchNames) {
            if (Objects.equals(branchName, currentBranchName)) {
                System.out.print("*");
            }
            System.out.println(branchName);
        }

        System.out.println();
        StagingArea stagingArea = StagingArea.fromFile();
        stagingArea.print();

        // todo:
        // === Modifications Not Staged For Commit ===
        // === Untracked Files ===
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

    private static boolean hasNoUntrackedFiles() {
        Folder currentFolder = Folder.fromHead();
        StagingArea stagingArea = StagingArea.fromFile();

        List<String> filenames = Utils.plainFilenamesIn(CWD);
        assert filenames != null;
        for (String filename : filenames) {
            if(!currentFolder.containsFile(filename) &&
                    !stagingArea.containsStagedAdd(filename)
            ) {
                return false;
            }
        }
        return true;
    }

    private static void resetWorkingDirectory(String commitSha) {
        Commit commit = Commit.fromSha(commitSha);
        if (commit == null) {
            return;
        }
        String folderSha = commit.getFolderSha();
        Folder folder = Folder.fromSha(folderSha);
        folder.writeToWorkingDirectory();

        StagingArea stagingArea = StagingArea.fromFile();
        stagingArea.clear();
        stagingArea.save();
    }

    public static void checkoutBranch(String branchName) {
        if (!hasNoUntrackedFiles()) {
            System.out.println("There is an untracked file in the way; delete it, or add and commit it first.");
            return;
        }
        String currentBranchName = Head.getBranchName();
        if (currentBranchName.equals(branchName)) {
            System.out.println("No need to checkout the current branch.");
            return;
        }

        Branch branch = Branch.fromBranchName(branchName);
        if (branch == null) {
            System.out.println("No such branch exists.");
            return;
        }
        String commitSha = branch.getCommitSha();
        Head.setBranchName(branchName);
        resetWorkingDirectory(commitSha);
    }

    public static void reset(String commitSha) {
        resetWorkingDirectory(commitSha);

        Branch branch = Branch.fromBranchName(Head.getBranchName());
        assert branch != null;
        branch.setCommitSha(commitSha);
        branch.save();
    }
}
