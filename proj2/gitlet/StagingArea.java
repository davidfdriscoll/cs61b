package gitlet;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.*;

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
    }

    public void removeFile(String filename, Folder currentFolder) {
        if (!stagedAdds.containsKey(filename) && !currentFolder.containsFile(filename)) {
            System.out.println("No reason to remove the file.");
            return;
        }
        stagedAdds.remove(filename);
        if (currentFolder.containsFile(filename)) {
            stagedRemoves.add(filename);
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

    public boolean isEmpty() {
        return stagedAdds.isEmpty() && stagedRemoves.isEmpty();
    }

    public static StagingArea createMergeStagingArea(
        Folder givenFolder,
        Folder lcaFolder,
        Folder currentFolder
    ) {
        Set<String> currentFiles = currentFolder.trackedFiles();
        Set<String> givenFiles = givenFolder.trackedFiles();
        Set<String> splitPointFiles = lcaFolder.trackedFiles();

        Set<String> allFiles = new HashSet<>();
        allFiles.addAll(givenFiles);
        allFiles.addAll(splitPointFiles);

        StagingArea stagingArea = new StagingArea();
        boolean encounteredMergeConflict = false;

        for (String file: allFiles) {
            String givenFileSha = givenFolder.getFileBlobSha(file);
            String lcaFileSha = lcaFolder.getFileBlobSha(file);
            String currentFileSha = currentFolder.getFileBlobSha(file);

            boolean added = !lcaFolder.containsFile(file)
                && givenFiles.contains(file)
                && !currentFiles.contains(file);
            boolean removed = lcaFolder.containsFile(file)
                && !givenFiles.contains(file)
                && Objects.equals(lcaFileSha, currentFileSha);
            boolean modified = !Objects.equals(lcaFileSha, givenFileSha);

            // added to given branch since split and not present in current branch -> add
            if (added) {
                FileBlob fileBlob = FileBlob.fromSha(givenFileSha);
                stagingArea.addFile(file, fileBlob, currentFolder);
                // removed from given branch since split and unmodified in current branch -> remove
            } else if (removed) {
                stagingArea.removeFile(file, currentFolder);
                // modified in given branch since split
            } else if (modified) {
                // unmodified in current branch -> replace with given branch version
                if (currentFolder.containsFile(file)
                        && Objects.equals(currentFileSha, lcaFileSha)
                ) {
                    FileBlob fileBlob = FileBlob.fromSha(givenFileSha);
                    stagingArea.addFile(file, fileBlob, currentFolder);
                    // modified in different ways: concat the two versions
                } else if (!Objects.equals(givenFileSha, currentFileSha)) {
                    encounteredMergeConflict = true;
                    FileBlob mergeFileBlob =
                        FileBlob.mergeConflictFileBlob(currentFileSha, givenFileSha);
                    stagingArea.addFile(file, mergeFileBlob, currentFolder);
                    mergeFileBlob.save();
                }
            }
        }

        if (encounteredMergeConflict) {
            System.out.println("Encountered a merge conflict.");
        }

        return stagingArea;
    }
}
