package gitlet;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

import static gitlet.Repository.GITLET_DIR;

public class Branch {
    private final String name;
    private String commitSha;

    public Branch(String name, String commitSha) {
        this.name = name;
        this.commitSha = commitSha;
    }

    private static Branch fromFolderAndName(File gitletDir, String name) {
        File branchFile = Utils.join(Repository.getHeadsDir(gitletDir), name);
        if (!branchFile.exists()) {
            return null;
        }
        String commitSha = Utils.readContentsAsString(branchFile);
        return new Branch(name, commitSha);
    }

    public static Branch fromBranchName(String name) {
        return fromFolderAndName(GITLET_DIR, name);
    }

    public static Branch fromRemote(Remote remote, String branchName) {
        File directory = remote.getRemotePath();
        return fromFolderAndName(directory, branchName);
    }

    public void save(File dir) {
        File branchFile = Utils.join(Repository.getHeadsDir(dir), name);
        if (!branchFile.exists()) {
            try {
                branchFile.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        Utils.writeContents(branchFile, commitSha);
    }

    public void save() {
        save(GITLET_DIR);
    }

    public void delete() {
        File branchFile = Utils.join(Repository.getHeadsDir(GITLET_DIR), name);
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

        String lcaSha =
            Commit.latestCommonAncestor(currentCommit, givenCommit, GITLET_DIR, GITLET_DIR);
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

        Commit newCommit =
            Commit.createMergeCommit(currentCommit, givenCommit, lcaCommit, givenBranch.name, name);

        setCommitSha(newCommit.getSha());
        save();
    }
}
