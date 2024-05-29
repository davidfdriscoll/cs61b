package gitlet;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static gitlet.Repository.*;

public class FileBlob {
    private byte[] contents;
    private String sha;

    FileBlob(byte[] contents) {
        this.contents = contents;
        this.sha = Utils.sha1(contents);
    }

    public String getContentAsString() {
        return new String(contents, StandardCharsets.UTF_8);
    }
    public String getSha() {
        return sha;
    }

    private void write(File location) {
        if (!location.exists()) {
            try {
                location.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        Utils.writeContents(location, contents);
    }

    public void save() {
        File blobFile = Utils.join(Repository.getFileBlobsDir(GITLET_DIR), sha);
        write(blobFile);
    }

    private static File getFileBlobFile(File folder, String sha) {
        try {
            Utils.join(folder, sha);
        } catch (NullPointerException e) {
            return null;
        }
        return Utils.join(folder, sha);
    }

    public static boolean shaExists(String sha) {
        File fileblobFile = getFileBlobFile(Repository.getFileBlobsDir(GITLET_DIR), sha);
        assert fileblobFile != null;
        return fileblobFile.exists();
    }

    public static FileBlob fromSha(String sha) {
        File fileblobFile = getFileBlobFile(Repository.getFileBlobsDir(GITLET_DIR), sha);
        assert fileblobFile != null;
        return new FileBlob(Utils.readContents(fileblobFile));
    }

    public static FileBlob fromRemoteSha(Remote remote, String sha) {
        File fileblobDir = getFileBlobsDir(remote.getRemotePath());
        File fileblobFile = getFileBlobFile(fileblobDir, sha);
        assert fileblobFile != null;
        return new FileBlob(Utils.readContents(fileblobFile));
    }

    public static FileBlob fromFilename(String filename) {
        File file = Utils.join(CWD, filename);
        if (!file.exists()) {
            return null;
        }
        byte[] fileContents = Utils.readContents(file);
        return new FileBlob(fileContents);
    }

    public static FileBlob mergeConflictFileBlob(String currentFileSha, String givenFileSha) {
        FileBlob currentFile = FileBlob.fromSha(currentFileSha);
        FileBlob givenFile = FileBlob.fromSha(givenFileSha);
        String currentFileString =
                currentFile == null ? "" : currentFile.getContentAsString();
        String givenFileString =
                givenFile == null ? "" : givenFile.getContentAsString();
        String mergeString =
                "<<<<<<< HEAD\n"
                        + currentFileString
                        + "=======\n"
                        + givenFileString
                        + ">>>>>>>\n";
        return new FileBlob(mergeString.getBytes(StandardCharsets.UTF_8));
    }

    public void writeToFile(String filename) {
        File workingLocation = Utils.join(CWD, filename);
        write(workingLocation);
    }
}
