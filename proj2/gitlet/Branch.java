package gitlet;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class Branch {
    private final String name;
    private String commitSha;

    public Branch(String name, String commitSha) {
        this.name = name;
        this.commitSha = commitSha;
    }

    public static Branch fromBranchName(String name) {
        File branchFile = Utils.join(Repository.HEADS_FOLDER, name);
        if (!branchFile.exists()) {
            return null;
        }
        String commitSha = Utils.readContentsAsString(branchFile);
        return new Branch(name, commitSha);
    }

    public void save() {
        File branchFile = Utils.join(Repository.HEADS_FOLDER, name);
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
        File branchFile = Utils.join(Repository.HEADS_FOLDER, name);
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
        new WorkingDirectory().reset(commitSha);
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

    private void fastForward(String givenCommitSha) {
        new WorkingDirectory().reset(givenCommitSha);
        setCommitSha(givenCommitSha);
        save();
        System.out.println("Current branch fast-forwarded.");
    }

    public void merge(Branch givenBranch) {
        if (Objects.equals(givenBranch.name, name)) {
            System.out.println("Cannot merge a branch with itself.");
            return;
        }

        Commit currentCommit = Commit.fromSha(getCommitSha());
        String givenCommitSha = givenBranch.getCommitSha();
        Commit givenCommit = Commit.fromSha(givenBranch.getCommitSha());
        assert currentCommit != null;
        assert givenCommit != null;

        String lcaSha = Commit.latestCommonAncestor(currentCommit, givenCommit);
        if (Objects.equals(givenCommitSha, lcaSha)) {
            System.out.println("Given branch is an ancestor of the current branch.");
            return;
        }
        if (Objects.equals(commitSha, lcaSha)) {
            fastForward(givenCommitSha);
            return;
        }

        Commit lcaCommit = Commit.fromSha(lcaSha);
        assert lcaCommit != null;

        Commit newCommit = Commit.createMergeCommit(currentCommit, givenCommit, lcaCommit, givenBranch.name, name);

        setCommitSha(newCommit.getSha());
        save();
    }
}
