package gitlet;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.*;

import static gitlet.Repository.CWD;
import static gitlet.Repository.GITLET_DIR;

public class Folder implements Serializable {
    // TreeMap<filename, fileblobsha>
    private final TreeMap<String, String> folder;

    Folder(TreeMap<String, String> folder) {
        this.folder = folder;
    }

    public static Folder emptyFolder() {
        TreeMap<String, String> emptyFolder = new TreeMap<>();
        return new Folder(emptyFolder);
    }

    public String generateSha() {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, String> entry : folder.entrySet()) {
            sb.append(entry.getKey());
            sb.append(entry.getValue());
        }
        return Utils.sha1(sb.toString());
    }

    public static Folder fromSha(String folderSha) {
        File folderFile = Utils.join(Repository.getFoldersDir(GITLET_DIR), folderSha);
        if (!folderFile.exists()) {
            return null;
        }
        return fromFile(folderFile);
    }

    public static Folder fromRemote(File remotePath, String folderSha) {
        File folderDir = Repository.getFoldersDir(remotePath);
        File folderFile = Utils.join(folderDir, folderSha);
        return fromFile(folderFile);
    }

    public static Folder fromFile(File file) {
        return Utils.readObject(file, Folder.class);
    }

    public static Folder fromHead() {
        String branchName = Head.getBranchName();
        Branch branch = Branch.fromBranchName(branchName);
        String currentCommitSha = branch.getCommitSha();
        Commit currentCommit = Commit.fromSha(currentCommitSha);
        String currentFolderSha = currentCommit.getFolderSha();
        return Folder.fromSha(currentFolderSha);
    }

    public void saveToSha(String sha) {
        saveToSha(GITLET_DIR, sha);
    }

    public void saveToSha(File dir, String sha) {
        File folderFile = Utils.join(Repository.getFoldersDir(dir), sha);
        saveToFile(folderFile);
    }

    public void saveToFile(File file) {
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        Utils.writeObject(file, this);
    }

    public void addFile(String filename, String fileSha) {
        folder.put(filename, fileSha);
    }
    public void removeFile(String filename) {
        folder.remove(filename);
    }

    public boolean containsFile(String filename) {
        return folder.containsKey(filename);
    }

    public String getFileBlobSha(String filename) {
        return folder.get(filename);
    }

    public void writeToWorkingDirectory(WorkingDirectory wd) {
        Set<String> untrackedFiles = wd.getUntrackedFilesSet();
        if (!Collections.disjoint(untrackedFiles, folder.keySet())) {
            System.out.println(
                "There is an untracked file in the way; delete it, or add and commit it first."
            );
            throw new RuntimeException("untracked file");
        }


        List<String> currentFilenames = wd.getFiles();
        for (String currentFilename: currentFilenames) {
            if (!containsFile(currentFilename)) {
                Utils.restrictedDelete(Utils.join(CWD, currentFilename));
            }
        }

        for (Map.Entry<String, String> entry : folder.entrySet()) {
            String filename = entry.getKey();
            String fileBlobSha = entry.getValue();
            FileBlob fileBlob = FileBlob.fromSha(fileBlobSha);
            assert fileBlob != null;
            fileBlob.writeToFile(filename);
        }
    }

    public Set<String> trackedFiles() {
        return folder.keySet();
    }
    public Map<String, String> folderMap() {
        return folder;
    }
}
