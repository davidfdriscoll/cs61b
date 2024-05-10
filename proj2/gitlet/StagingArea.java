package gitlet;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.*;

import static gitlet.Repository.CWD;
import static gitlet.Repository.GITLET_DIR;

public class StagingArea implements Serializable {
    static final File STAGING_AREA_FILE = Utils.join(GITLET_DIR, "staging_area");

    // HashMap<filename, fileblobsha>
    private HashMap<String, String> stagedAdds;
    // HashMap<filename>
    private HashSet<String> stagedRemoves;

    public StagingArea() {
        this.stagedAdds = new HashMap<>();
        this.stagedRemoves = new HashSet<>();
    }

    public void clear() {
        this.stagedAdds = new HashMap<>();
        this.stagedRemoves = new HashSet<>();
    }

    public static StagingArea fromFile() {
        return Utils.readObject(STAGING_AREA_FILE, StagingArea.class);
    }

    public void save() {
        if (!STAGING_AREA_FILE.exists()) {
            try {
                STAGING_AREA_FILE.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        Utils.writeObject(STAGING_AREA_FILE, this);
    }

    public void addFile(String filename, FileBlob fileBlob, Folder currentFolder) {
        // if staged for removal, remove from removals
        stagedRemoves.remove(filename);
        // if current commit contains the identical file, remove from staging area
        if (currentFolder.containsFile(filename)
                && Objects.equals(currentFolder.getFileBlobSha(filename), fileBlob.getSha())
        ) {
            stagedAdds.remove(filename);
        // otherwise add to staging area
        } else {
            stagedAdds.put(filename, fileBlob.getSha());
        }
        Utils.writeObject(STAGING_AREA_FILE, stagedAdds);
    }

    public void removeFile(String filename, Folder currentFolder) {
        if (!stagedAdds.containsKey(filename) && !currentFolder.containsFile(filename)) {
            System.out.println("No reason to remove the file.");
            return;
        }
        if (stagedAdds.containsKey(filename)) {
            stagedAdds.remove(filename);
        }
        if (currentFolder.containsFile(filename)) {
            stagedRemoves.add(filename);
            File workingDirectoryFile = Utils.join(CWD, filename);
            Utils.restrictedDelete(workingDirectoryFile);
        }
    }

    public Folder updateFolder(Folder folder) {
        for (Map.Entry<String, String> add : stagedAdds.entrySet()) {
            folder.addFile(add.getKey(), add.getValue());
        }
        for (String filenameToRemove : stagedRemoves) {
            folder.removeFile(filenameToRemove);
        }
        return folder;
    }

    public void print() {
        System.out.println("=== Staged Files ===");
        List<String> sortedAdds = new ArrayList<>(stagedAdds.keySet());
        Collections.sort(sortedAdds);
        for (String add : sortedAdds) {
            System.out.println(add);
        }

        System.out.println();
        System.out.println("=== Removed Files ===");
        List<String> sortedRemoves = new ArrayList<>(stagedRemoves);
        Collections.sort(sortedRemoves);
        for (String remove : sortedRemoves) {
            System.out.println(remove);
        }
        System.out.println();
    }

    public Set<String> stagedAdds() {
        return stagedAdds.keySet();
    }
    public boolean containsStagedAdd(String filename) {
        return stagedAdds.containsKey(filename);
    }
    public boolean containsStagedRemove(String filename) {
        return stagedRemoves.contains(filename);
    }
    public boolean isEmpty() {
        return stagedAdds.isEmpty() && stagedRemoves.isEmpty();
    }
}
