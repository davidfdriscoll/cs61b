package gitlet;

import java.io.File;
import java.util.Date;
import java.util.Objects;

import static gitlet.Commit.COMMITS_FOLDER;
import static gitlet.FileBlob.FILEBLOBS_FOLDER;
import static gitlet.Folder.FOLDERS_FOLDER;
import static gitlet.Utils.*;

/** Represents a gitlet repository.
 *
 *  @author David Driscoll
 */
public class Repository {

    /** The current working directory. */
    public static final File CWD = new File(System.getProperty("user.dir"));
    /** The .gitlet directory. */
    public static final File GITLET_DIR = join(CWD, ".gitlet");
    /** objects folder within .gitlet directory */
    public static File OBJECTS_FOLDER = Utils.join(GITLET_DIR, "objects");

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

        Folder emptyFolder = Folder.emptyFolder();
        String emptyFolderSha = emptyFolder.generateSha();
        emptyFolder.saveToSha(emptyFolderSha);
        StagingArea.setFolder(emptyFolder);

        Commit initialCommit = Commit.initialCommit();
        initialCommit.save();
        Head.setCommitSha(initialCommit.getSha());
    }

    public static void add(String filename) {
        File file = Utils.join(CWD, filename);
        if (!file.exists()) {
            System.out.println("File does not exist.");
            return;
        }
        byte[] fileContents = Utils.readContents(file);

        FileBlob blob = new FileBlob(fileContents);
        blob.save();

        Folder stagingArea = StagingArea.getFolder();
        stagingArea.addFileBlob(filename, blob.getSha());
        StagingArea.setFolder(stagingArea);
    }

    public static void commit(String message) {
        String currentCommitSha = Head.getCommitSha();
        Commit currentCommit = Commit.fromSha(currentCommitSha);
        String currentCommitFolderSha = currentCommit.getFolderSha();

        Folder stagingArea = StagingArea.getFolder();
        String stagingAreaSha = stagingArea.generateSha();
        if (Objects.equals(stagingAreaSha, currentCommitFolderSha)) {
            System.out.println("No changes added to the commit.");
            return;
        }
        stagingArea.saveToSha(stagingAreaSha);

        Long timestamp = new Date().getTime();
        Commit newCommit = new Commit(
            message, stagingAreaSha, currentCommitSha, "-1", timestamp
        );
        newCommit.save();

        Head.setCommitSha(newCommit.getSha());
    }

    public static void log() {
        String commitSha = Head.getCommitSha();

        while (!Objects.equals(commitSha, "-1")) {
            Commit commit = Commit.fromSha(commitSha);
            commit.print();
            commitSha = commit.getParentSha();
        }

    }

    public static void checkoutFileFromHead(String filename) {
        checkoutFileFromCommit(Head.getCommitSha(), filename);
    }

    public static void checkoutFileFromCommit(String commitSha, String filename) {
        Commit commit = Commit.fromSha(commitSha);
        String folderSha = commit.getFolderSha();
        Folder folder = Folder.fromSha(folderSha);
        if (!folder.containsFileBlobSha(filename)) {
            System.out.println("File does not exist in that commit");
            throw new RuntimeException();
        }
        String fileBlobSha = folder.getFileBlobSha(filename);
        FileBlob fileblob = FileBlob.fromSha(fileBlobSha);
        fileblob.writeToFile(filename);
    }
}
