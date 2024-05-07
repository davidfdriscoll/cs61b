package gitlet;

import java.io.File;
import java.io.IOException;

import static gitlet.Repository.REFS_FOLDER;

public class Branch {
    public static File HEADS_FOLDER = Utils.join(REFS_FOLDER, "heads");

    private String name;
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
}
