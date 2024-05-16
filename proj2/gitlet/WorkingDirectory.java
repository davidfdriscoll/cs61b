package gitlet;

import java.util.*;

import static gitlet.Repository.CWD;
import static gitlet.Repository.add;

public class WorkingDirectory {
    private final List<String> files;
    private final Set<String> modifiedFiles = new HashSet<>();
    private final Set<String> unmodifiedFiles = new HashSet<>();
    private final Set<String> addedFiles = new HashSet<>();
    private final Set<String> removedFiles = new HashSet<>();

    WorkingDirectory() {
        Folder currentFolder = Folder.fromHead();
        StagingArea stagingArea = StagingArea.fromFile();
        Folder updatedFolder = stagingArea.updateFolder(currentFolder);
        Set<String> trackedSet = updatedFolder.trackedFiles();

        files = Utils.plainFilenamesIn(CWD);
        assert files != null;

        Set<String> workingSet = new HashSet<>(files);
        Set<String> allSet = new HashSet<>(workingSet);
        allSet.addAll(trackedSet);

        for (String file: allSet) {
            // added file
            if (workingSet.contains(file) && !trackedSet.contains(file)) {
                addedFiles.add(file);
            // removed file
            } else if (!workingSet.contains(file) && trackedSet.contains(file)) {
                removedFiles.add(file);
            } else if (workingSet.contains(file) && trackedSet.contains(file)) {
                String trackedSha = currentFolder.getFileBlobSha(file);
                String workingSha = FileBlob.fromFilename(file).getSha();
                // unmodified file
                if (Objects.equals(trackedSha, workingSha)) {
                    unmodifiedFiles.add(file);
                // modified file
                } else {
                    modifiedFiles.add(file);
                }
            }
        }
    }

    public List<String> getModificationsNotStagedForCommit() {
        Set<String> modificationSet = new HashSet<>(modifiedFiles);
        modificationSet.addAll(removedFiles);
        List<String> modificationList = new ArrayList<>();
        for (String file: modificationSet) {
            if (modifiedFiles.contains(file)) {
                modificationList.add(file + " (modified)");
            } else if (removedFiles.contains(file)) {
                modificationList.add(file + " (deleted)");
            }
        }
        Collections.sort(modificationList);
        return modificationList;
    }

    public List<String> getUntrackedFiles() {
        List<String> untrackedFiles = new ArrayList<>(addedFiles);
        Collections.sort(untrackedFiles);
        return untrackedFiles;
    }

    public Set<String> getUntrackedFilesSet() {
        return addedFiles;
    }

    public List<String> getFiles() {
        return files;
    }

    public static void reset(String commitSha) {
        Commit commit = Commit.fromSha(commitSha);
        if (commit == null) {
            return;
        }
        String folderSha = commit.getFolderSha();
        Folder folder = Folder.fromSha(folderSha);
        folder.writeToWorkingDirectory();

        StagingArea stagingArea = StagingArea.fromFile();
        stagingArea.clear();
        stagingArea.save();
    }
}
