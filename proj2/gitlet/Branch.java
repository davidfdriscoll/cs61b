package gitlet;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import static gitlet.Repository.REFS_FOLDER;

public class Branch {
    public static File HEADS_FOLDER = Utils.join(REFS_FOLDER, "heads");

    private final String name;
    private String commitSha;

    public Branch(String name, String commitSha) {
        this.name = name;
        this.commitSha = commitSha;
    }

    public static Branch fromBranchName(String name) {
        File branchFile = Utils.join(HEADS_FOLDER, name);
        if (!branchFile.exists()) {
            return null;
        }
        String commitSha = Utils.readContentsAsString(branchFile);
        return new Branch(name, commitSha);
    }

    public void save() {
        File branchFile = Utils.join(HEADS_FOLDER, name);
        if (!branchFile.exists()) {
            try {
                branchFile.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        Utils.writeContents(branchFile, commitSha);
    }

    public void delete() {
        File branchFile = Utils.join(HEADS_FOLDER, name);
        branchFile.delete();
    }

    public void setCommitSha(String commitSha) {
        this.commitSha = commitSha;
    }

    public String getCommitSha() {
        return commitSha;
    }

    public static void checkout(Branch branch) {
        String currentBranchName = Head.getBranchName();
        if (currentBranchName.equals(branch.name)) {
            System.out.println("No need to checkout the current branch.");
            return;
        }

        String commitSha = branch.getCommitSha();
        WorkingDirectory.reset(commitSha);
        Head.setBranchName(branch.name);
    }

    public static void checkout(String branchName) {
        Branch branch = Branch.fromBranchName(branchName);
        if (branch == null) {
            System.out.println("No such branch exists.");
            return;
        }
        checkout(branch);
    }

    public void merge(Branch givenBranch) {
        Commit currentCommit = Commit.fromSha(getCommitSha());
        String givenCommitSha = givenBranch.getCommitSha();
        Commit givenCommit = Commit.fromSha(givenCommitSha);
        assert currentCommit != null;
        assert givenCommit != null;

        String lcaSha = Commit.latestCommonAncestor(currentCommit, givenCommit);
        if (Objects.equals(givenCommitSha, lcaSha)) {
            System.out.println("Given branch is an ancestor of the current branch.");
            return;
        }
        if (Objects.equals(commitSha, lcaSha)) {
            checkout(givenBranch);
            System.out.println("Current branch fast-forwarded.");
            return;
        }

        Commit lca = Commit.fromSha(lcaSha);
        assert lca != null;

        Folder currentFolder = Folder.fromSha(currentCommit.getFolderSha());
        Folder lcaFolder = Folder.fromSha(lca.getFolderSha());
        Folder givenFolder = Folder.fromSha(givenCommit.getFolderSha());

        Set<String> currentFiles = currentFolder.trackedFiles();
        Set<String> givenFiles = givenFolder.trackedFiles();
        Set<String> splitPointFiles = lcaFolder.trackedFiles();

        Set<String> allFiles = new HashSet<>();
        allFiles.addAll(givenFiles);
        allFiles.addAll(splitPointFiles);

        StagingArea stagingArea = StagingArea.fromFile();

        for (String file: allFiles) {
            // added to given branch since split and not present in current branch -> add
            if (givenFiles.contains(file) && !currentFiles.contains(file)) {
                stagingArea.a
            }
            // removed from given branch since split and unmodified in current branch -> remove
            // modified in given branch since split
                // modified in the same way (have same content or removed) -> do nothing
                // unmodified in current branch -> replace with given branch version
                // modified in different ways: concat the two versions
        }
    }
}
