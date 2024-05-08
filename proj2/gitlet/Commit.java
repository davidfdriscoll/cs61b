package gitlet;

// TODO: any imports you need here

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Objects;

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
    public String getMessage() { return message; }

    public static Commit fromSha(String commitSha) {
        File commitFile = findCommitPath(commitSha);
        if (commitFile == null || !commitFile.exists()) {
            System.out.println("No commit with that id exists.");
            return null;
        }
        return Utils.readObject(commitFile, Commit.class);
    }

    private static File findCommitPath(String commitSha) {
        String prefix = commitSha.substring(0, 2);
        File prefixFolder = Utils.join(COMMITS_FOLDER, prefix);
        if (!prefixFolder.exists()) {
            return null;
        }
        if (commitSha.length() > 40) {
            return null;
        } else if (commitSha.length() == 40) {
            return Utils.join(prefixFolder, commitSha);
        } else {
            List<String> candidateShas = Utils.plainFilenamesIn(prefixFolder);
            assert candidateShas != null;
            for (String candidateSha: candidateShas) {
                if (candidateSha.startsWith(commitSha))
                    return Utils.join(prefixFolder, candidateSha);
            }
            return null;
        }
    }

    private static File createCommitPath(String commitSha) {
        String prefix = commitSha.substring(0, 2);
        File prefixFolder = Utils.join(COMMITS_FOLDER, prefix);
        if (!prefixFolder.exists()) {
            prefixFolder.mkdir();
        }
        return Utils.join(prefixFolder, commitSha);
    }

    public void save() {
        File commitFile = createCommitPath(sha);
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
        if (!Objects.equals(mergeParentSha, "-1")) {
            System.out.println("Merge: " + parentSha.substring(0, 7) + " " + mergeParentSha.substring(0, 7));
        }
        System.out.println("Date: " + simpleDateFormat.format(date));
        System.out.println(message);
        System.out.println();
    }
}
