package gitlet;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.TreeMap;

import static gitlet.Repository.OBJECTS_FOLDER;

public class Folder implements Serializable {
    public static File FOLDERS_FOLDER = Utils.join(OBJECTS_FOLDER, "folders");

    private TreeMap<String, String> folder;

    Folder(TreeMap<String, String> folder) {
        this.folder = folder;
    }

    public static Folder emptyFolder() {
        TreeMap<String, String> emptyFolder = new TreeMap<>();
        return new Folder(emptyFolder);
    }

    public static String generateSha(Folder folder) {
        return Utils.sha1(folder.folder.navigableKeySet().toArray());
    }

    public static Folder fromFile(String commitSha) {
        File folderFile = Utils.join(FOLDERS_FOLDER, commitSha);
        return Utils.readObject(folderFile, Folder.class);
    }

    public void save() {
        String sha = generateSha(this);
        File folderFile = Utils.join(FOLDERS_FOLDER, sha);
        if (!folderFile.exists()) {
            try {
                folderFile.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        Utils.writeObject(folderFile, this);
    }
}
