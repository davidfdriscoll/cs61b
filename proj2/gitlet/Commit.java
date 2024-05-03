package gitlet;

// TODO: any imports you need here

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

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
    private String sha;

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
        this.sha = Utils.sha1(
                    message,
                    folderSha,
                    parentSha,
                    mergeParentSha,
                    timestamp.toString()
                );
    }

    public static Commit initialCommit() {
        Folder emptyFolder = Folder.emptyFolder();
        String emptyFolderSha = emptyFolder.generateSha();
        return new Commit(
            "initial commit",
            emptyFolderSha,
            "-1",
            "-1",
            0L
        );
    }

    public String getSha() {
        return sha;
    }
    public String getParentSha() { return parentSha; }
    public String getFolderSha() { return folderSha; }

    public static Commit fromSha(String commitSha) {
        File commitFile = Utils.join(COMMITS_FOLDER, commitSha);
        if (!commitFile.exists()) {
            System.out.println("No commit with that id exists.");
            throw new RuntimeException();
        }
        return Utils.readObject(commitFile, Commit.class);
    }

    public void save() {
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

    public void print() {
        Date date = new Date(timestamp);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("E MMM d hh:mm:ss yyyy Z");

        System.out.println("===");
        System.out.println("commit " + sha);
        System.out.println("Date: " + simpleDateFormat.format(date));
        System.out.println(message);
        System.out.println();
    }
}
