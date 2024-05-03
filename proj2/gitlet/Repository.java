package gitlet;

import java.io.File;
import java.io.IOException;

import static gitlet.Commit.COMMITS_FOLDER;
import static gitlet.Folder.FOLDERS_FOLDER;
import static gitlet.Utils.*;

// TODO: any imports you need here

/** Represents a gitlet repository.
 *  TODO: It's a good idea to give a description here of what else this Class
 *  does at a high level.
 *
 *  @author TODO
 */
public class Repository {
    /**
     * TODO: add instance variables here.
     *
     * List all instance variables of the Repository class here with a useful
     * comment above them describing what that variable represents and how that
     * variable is used. We've provided two examples for you.
     */

    /** The current working directory. */
    public static final File CWD = new File(System.getProperty("user.dir"));
    /** The .gitlet directory. */
    public static final File GITLET_DIR = join(CWD, ".gitlet");

    public static File HEAD_FILE = Utils.join(GITLET_DIR, "HEAD");
    public static File OBJECTS_FOLDER = Utils.join(GITLET_DIR, "objects");
    public static File FILES_FOLDER = Utils.join(OBJECTS_FOLDER, "files");

    /* TODO: fill in the rest of this class. */
    public static void init() {
        if (GITLET_DIR.exists()) {
            System.out.println("A Gitlet version-control system already exists in the current directory.");
            return;
        }
        GITLET_DIR.mkdir();
        OBJECTS_FOLDER.mkdir();
        COMMITS_FOLDER.mkdir();
        FILES_FOLDER.mkdir();
        FOLDERS_FOLDER.mkdir();
        Folder emptyFolder = Folder.emptyFolder();
        emptyFolder.save();
        StagingArea.setFolderSha(Folder.generateSha(emptyFolder));
        Commit initialCommit = Commit.initialCommit();
        initialCommit.save();
    }
}
