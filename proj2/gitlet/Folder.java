package gitlet;

import java.io.File;
import java.util.TreeMap;

import static gitlet.Repository.OBJECTS_FOLDER;

public class Folder {
    public static File FOLDERS_FOLDER = Utils.join(OBJECTS_FOLDER, "folders");

    private String sha;
    private TreeMap<String, String> folder;

    Folder(TreeMap<String, String> folder) {
        this.folder = folder;
        this.sha = Utils.sha1(folder);
    }
}
