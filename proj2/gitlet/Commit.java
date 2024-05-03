package gitlet;

// TODO: any imports you need here

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.Date; // TODO: You'll likely use this in this class

import static gitlet.Repository.OBJECTS_FOLDER;

/** Represents a gitlet commit object.
 *  TODO: It's a good idea to give a description here of what else this Class
 *  does at a high level.
 *
 *  @author TODO
 */
public class Commit implements Serializable {
    /**
     * TODO: add instance variables here.
     *
     * List all instance variables of the Commit class here with a useful
     * comment above them describing what that variable represents and how that
     * variable is used. We've provided one example for `message`.
     */

    public static File COMMITS_FOLDER = Utils.join(OBJECTS_FOLDER, "commits");

    /** The message of this Commit. */
    private String message;

    private String folderSha;
    private String parentSha;
    private String mergeParentSha;
    private Long timestamp;

    /* TODO: fill in the rest of this class. */
    Commit(
            String message,
            String folderSha,
            String parentSha,
            String mergeParentSha,
            Long timestamp
    ) {
        this.message = message;
        this.folderSha = folderSha;
        this.parentSha = parentSha;
        this.mergeParentSha = mergeParentSha;
        this.timestamp = timestamp;
    }

    public static Commit initialCommit() {
        Folder emptyFolder = Folder.emptyFolder();
        String emptyFolderSha = Folder.generateSha(emptyFolder);
        return new Commit(
            "initial commit",
            emptyFolderSha,
            "-1",
            "-1",
            0L
        );
    }

    public static String generateSha(Commit commit) {
        return Utils.sha1(
            commit.message,
            commit.folderSha,
            commit.parentSha,
            commit.mergeParentSha,
            commit.timestamp.toString()
        );
    }

    public static Commit fromFile(String commitSha) {
        File commitFile = Utils.join(COMMITS_FOLDER, commitSha);
        return Utils.readObject(commitFile, Commit.class);
    }

    public void save() {
        String sha = generateSha(this);
        File commitFile = Utils.join(COMMITS_FOLDER, sha);
        if (!commitFile.exists()) {
            try {
                commitFile.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        Utils.writeObject(commitFile, this);
    }
}
