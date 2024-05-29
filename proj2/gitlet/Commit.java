package gitlet;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.*;

/** Represents a gitlet commit object.
 */
public class Commit implements Serializable {
    /** The message of this Commit. */
    private String message;

    private String folderSha;
    private String parentSha;
    private String mergeParentSha;
    private Long timestamp;
    private String sha;

    Commit(
        String message,
        String folderSha,
        String parentSha,
        String mergeParentSha,
        Long timestamp
    ) {
        this.message = message;
        this.folderSha = folderSha;
        this.parentSha = parentSha;
        this.mergeParentSha = mergeParentSha;
        this.timestamp = timestamp;
        this.sha = Utils.sha1(
            message,
            folderSha,
            parentSha,
            mergeParentSha,
            timestamp.toString()
        );
    }

    public static Commit initialCommit() {
        Folder emptyFolder = Folder.emptyFolder();
        String emptyFolderSha = emptyFolder.generateSha();
        return new Commit(
            "initial commit",
            emptyFolderSha,
            "-1",
            "-1",
            0L
        );
    }

    public String getSha() {
        return sha;
    }
    public String getParentSha() {
        return parentSha;
    }
    public String getMergeParentSha() {
        return mergeParentSha;
    }
    public String getFolderSha() {
        return folderSha;
    }
    public String getMessage() {
        return message;
    }

    public static boolean doesShaExist(String commitSha) {
        File commitFile = findCommitPath(Repository.getCommitsDir(Repository.GITLET_DIR), commitSha);
        assert commitFile != null;
        return commitFile.exists();
    }

    public static Commit fromSha(String commitSha) {
        File commitFile = findCommitPath(Repository.getCommitsDir(Repository.GITLET_DIR), commitSha);
        if (commitFile == null || !commitFile.exists()) {
            System.out.println("No commit with that id exists.");
            throw new RuntimeException();
        }
        return Utils.readObject(commitFile, Commit.class);
    }

    public static Commit fromRemote(Remote remote, String commitSha) {
        File commitsDir = Repository.getCommitsDir(remote.getRemotePath());
        File commitFile = findCommitPath(commitsDir , commitSha);
        return Utils.readObject(commitFile, Commit.class);
    }

    private static File findCommitPath(File folder, String commitSha) {
        String prefix = commitSha.substring(0, 2);
        File prefixFolder = Utils.join(folder, prefix);
        if (!prefixFolder.exists()) {
            return null;
        }
        if (commitSha.length() > Utils.UID_LENGTH) {
            return null;
        } else if (commitSha.length() == Utils.UID_LENGTH) {
            return Utils.join(prefixFolder, commitSha);
        } else {
            List<String> candidateShas = Utils.plainFilenamesIn(prefixFolder);
            assert candidateShas != null;
            for (String candidateSha: candidateShas) {
                if (candidateSha.startsWith(commitSha)) {
                    return Utils.join(prefixFolder, candidateSha);
                }
            }
            return null;
        }
    }

    private static File createCommitPath(String commitSha) {
        String prefix = commitSha.substring(0, 2);
        File prefixFolder = Utils.join(Repository.getCommitsDir(Repository.GITLET_DIR), prefix);
        if (!prefixFolder.exists()) {
            prefixFolder.mkdir();
        }
        return Utils.join(prefixFolder, commitSha);
    }

    public static List<String> findAllCommitShas() {
        String[] prefixes = Repository.getCommitsDir(Repository.GITLET_DIR).list();
        List<String> commitShas = new ArrayList<>();
        assert prefixes != null;
        for (String prefix: prefixes) {
            File prefixFolder = Utils.join(Repository.getCommitsDir(Repository.GITLET_DIR), prefix);
            List<String> prefixedFiles = Utils.plainFilenamesIn(prefixFolder);
            assert prefixedFiles != null;
            commitShas.addAll(prefixedFiles);
        }
        return commitShas;
    }

    public void save() {
        File commitFile = createCommitPath(sha);
        if (!commitFile.exists()) {
            try {
                commitFile.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        Utils.writeObject(commitFile, this);
    }

    public void print() {
        Date date = new Date(timestamp);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("E MMM d hh:mm:ss yyyy Z");

        System.out.println("===");
        System.out.println("commit " + sha);
        if (!Objects.equals(mergeParentSha, "-1")) {
            System.out.println(
                "Merge: "
                + parentSha.substring(0, 7)
                + " "
                + mergeParentSha.substring(0, 7)
            );
        }
        System.out.println("Date: " + simpleDateFormat.format(date));
        System.out.println(message);
        System.out.println();
    }

    private static void addAncestorsToQueue(String commitSha, Deque<String> queue) {
        Commit commit = Commit.fromSha(commitSha);
        assert commit != null;
        if (!Objects.equals(commit.getParentSha(), "-1")) {
            queue.add(commit.getParentSha());
        }
        if (!Objects.equals(commit.getMergeParentSha(), "-1")) {
            queue.add(commit.getMergeParentSha());
        }
    }

    public static String latestCommonAncestor(Commit left, Commit right) {
        Set<String> leftAncestry = new HashSet<>();

        Deque<String> queue = new ArrayDeque<>();
        String sha = left.getSha();
        if (!Objects.equals(sha, "-1")) {
            queue.add(sha);
        }
        while (!queue.isEmpty()) {
            sha = queue.removeFirst();
            leftAncestry.add(sha);
            addAncestorsToQueue(sha, queue);
        }

        sha = right.getSha();
        if (!Objects.equals(sha, "-1")) {
            queue.add(sha);
        }
        while (!queue.isEmpty()) {
            sha = queue.removeFirst();
            if (leftAncestry.contains(sha)) {
                return sha;
            }
            addAncestorsToQueue(sha, queue);
        }
        return null;
    }

    public static Commit createMergeCommit(
        Commit currentCommit,
        Commit givenCommit,
        Commit lcaCommit,
        String givenBranchName,
        String currentBranchName
    ) {
        Folder currentFolder = Folder.fromSha(currentCommit.getFolderSha());
        Folder lcaFolder = Folder.fromSha(lcaCommit.getFolderSha());
        Folder givenFolder = Folder.fromSha(givenCommit.getFolderSha());

        StagingArea mergedStagingArea = StagingArea.createMergeStagingArea(
            givenFolder, lcaFolder, currentFolder
        );

        Folder newFolder = mergedStagingArea.updateFolder(currentFolder);
        String newFolderSha = newFolder.generateSha();

        Long timestamp = new Date().getTime();
        Commit newCommit = new Commit(
            "Merged " + givenBranchName + " into " + currentBranchName + ".",
            newFolderSha,
            currentCommit.getSha(),
            givenCommit.getSha(),
            timestamp
        );

        // after creating all the objects, save to disk, starting with updating the working directory
        // (which will throw exception if there is a file in the way)
        newFolder.writeToWorkingDirectory(new WorkingDirectory());
        newFolder.saveToSha(newFolderSha);
        newCommit.save();
        return newCommit;
    }
}
