package gitlet;

import java.io.File;
import java.io.IOException;

public class Remote {
    private final String name;
    private String remotePath;

    public Remote(String name, String commitSha) {
        this.name = name;
        this.remotePath = commitSha;
    }

    public static Remote fromRemoteName(String name) {
        File remoteFile = Utils.join(Repository.REMOTES_FOLDER, name);
        if (!remoteFile.exists()) {
            return null;
        }
        String remotePath = Utils.readContentsAsString(remoteFile);
        return new Remote(name, remotePath);
    }

    public void save() {
        File remoteFile = Utils.join(Repository.REMOTES_FOLDER, name);
        if (!remoteFile.exists()) {
            try {
                remoteFile.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        Utils.writeContents(remoteFile, remotePath);
    }

    public void delete() {
        File remoteFile = Utils.join(Repository.REMOTES_FOLDER, name);
        remoteFile.delete();
    }
}
