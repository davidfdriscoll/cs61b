package gitlet;

import java.io.File;
import java.io.IOException;

import static gitlet.Repository.GITLET_DIR;

public class StagingArea {
    static final File STAGING_AREA_FILE = Utils.join(GITLET_DIR, "staging_area");

    public static String getFolderSha() {
        if (!STAGING_AREA_FILE.exists()) {
            throw new RuntimeException("staging area file does not exist on attempted read");
        }
        return Utils.readContentsAsString(STAGING_AREA_FILE);
    }

    public static void setFolderSha(String folderSha) {
        if (!STAGING_AREA_FILE.exists()) {
            try {
                STAGING_AREA_FILE.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        Utils.writeContents(STAGING_AREA_FILE, folderSha);
    }
}
