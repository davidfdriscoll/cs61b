package gitlet;

import java.io.File;
import java.io.IOException;

import static gitlet.Repository.GITLET_DIR;

public class Head {
    static final File HEAD_FILE = Utils.join(GITLET_DIR, "HEAD");

    public static String getCommitSha() {
        if (!HEAD_FILE.exists()) {
            throw new RuntimeException("head file does not exist on attempted read");
        }
        return Utils.readContentsAsString(HEAD_FILE);
    }

    public static void setCommitSha(String commitSha) {
        if (!HEAD_FILE.exists()) {
            try {
                HEAD_FILE.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        Utils.writeContents(HEAD_FILE, commitSha);
    }
}
