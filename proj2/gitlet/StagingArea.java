package gitlet;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static gitlet.Repository.GITLET_DIR;

public class StagingArea {
    static final File STAGING_AREA_FILE = Utils.join(GITLET_DIR, "staging_area");
    private static HashMap<String, String> stagedAdds;

    public static void clear() {
        if (!STAGING_AREA_FILE.exists()) {
            try {
                STAGING_AREA_FILE.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        stagedAdds = new HashMap<>();
        Utils.writeObject(STAGING_AREA_FILE, stagedAdds);
    }

    @SuppressWarnings("unchecked")
    private static void readMapFromFile() {
        stagedAdds = Utils.readObject(STAGING_AREA_FILE, HashMap.class);
    }

    public static void addFile(String filename, FileBlob fileBlob) {
        readMapFromFile();
        stagedAdds.put(filename, fileBlob.getSha());
        Utils.writeObject(STAGING_AREA_FILE, stagedAdds);
    }

    public static Folder updateFolder(Folder folder) {
        readMapFromFile();
        for (Map.Entry<String, String> add : stagedAdds.entrySet()) {
            folder.addFile(add.getKey(), add.getValue());
        }
        return folder;
    }
}
