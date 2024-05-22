package gitlet;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static gitlet.Repository.CWD;

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
        File blobFile = Utils.join(Repository.FILEBLOBS_FOLDER, sha);
        write(blobFile);
    }

    public static FileBlob fromSha(String sha) {
        try {
            Utils.join(Repository.FILEBLOBS_FOLDER, sha);
        } catch (NullPointerException e) {
            return null;
        }
        File fileblobFile = Utils.join(Repository.FILEBLOBS_FOLDER, sha);
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

    public void print() {
        System.out.println(new String(contents, StandardCharsets.UTF_8));
    }
}
