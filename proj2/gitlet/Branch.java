package gitlet;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class Branch {
    private final String name;
    private String commitSha;

    public Branch(String name, String commitSha) {
        this.name = name;
        this.commitSha = commitSha;
    }

    public static Branch fromBranchName(String name) {
        File branchFile = Utils.join(Repository.HEADS_FOLDER, name);
        if (!branchFile.exists()) {
            return null;
        }
        String commitSha = Utils.readContentsAsString(branchFile);
        return new Branch(name, commitSha);
    }

    public void save() {
        File branchFile = Utils.join(Repository.HEADS_FOLDER, name);
        if (!branchFile.exists()) {
            try {
                branchFile.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        Utils.writeContents(branchFile, commitSha);
    }

    public void delete() {
        File branchFile = Utils.join(Repository.HEADS_FOLDER, name);
        branchFile.delete();
    }

    public void setCommitSha(String commitSha) {
        this.commitSha = commitSha;
    }

    public String getCommitSha() {
        return commitSha;
    }

    public static void checkout(Branch branch) {
        String currentBranchName = Head.getBranchName();
        if (currentBranchName.equals(branch.name)) {
            System.out.println("No need to checkout the current branch.");
            return;
        }

        String commitSha = branch.getCommitSha();
        new WorkingDirectory().reset(commitSha);
        Head.setBranchName(branch.name);
    }

    public static void checkout(String branchName) {
        Branch branch = Branch.fromBranchName(branchName);
        if (branch == null) {
            System.out.println("No such branch exists.");
            return;
        }
        checkout(branch);
    }

    private void fastForward(String givenCommitSha) {
        new WorkingDirectory().reset(givenCommitSha);
        setCommitSha(givenCommitSha);
        save();
        System.out.println("Current branch fast-forwarded.");
    }

    private StagingArea createMergeStagingArea(
            Set<String> allFiles,
            StagingArea stagingArea,
            Folder givenFolder,
            Folder lcaFolder,
            Folder currentFolder,
            Set<String> givenFiles,
            Set<String> currentFiles) {
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

    public void merge(Branch givenBranch, StagingArea stagingArea) {
        Commit currentCommit = Commit.fromSha(getCommitSha());
        String givenCommitSha = givenBranch.getCommitSha();
        Commit givenCommit = Commit.fromSha(givenBranch.getCommitSha());
        assert currentCommit != null;
        assert givenCommit != null;

        String lcaSha = Commit.latestCommonAncestor(currentCommit, givenCommit);
        if (Objects.equals(givenCommitSha, lcaSha)) {
            System.out.println("Given branch is an ancestor of the current branch.");
            return;
        }
        if (Objects.equals(commitSha, lcaSha)) {
            fastForward(givenCommitSha);
            return;
        }

        Commit lca = Commit.fromSha(lcaSha);
        assert lca != null;

        Folder currentFolder = Folder.fromSha(currentCommit.getFolderSha());
        Folder lcaFolder = Folder.fromSha(lca.getFolderSha());
        Folder givenFolder = Folder.fromSha(givenCommit.getFolderSha());

        Set<String> currentFiles = currentFolder.trackedFiles();
        Set<String> givenFiles = givenFolder.trackedFiles();
        Set<String> splitPointFiles = lcaFolder.trackedFiles();

        Set<String> allFiles = new HashSet<>();
        allFiles.addAll(givenFiles);
        allFiles.addAll(splitPointFiles);

        StagingArea mergedStagingArea = createMergeStagingArea(
            allFiles, stagingArea, givenFolder, lcaFolder, currentFolder, givenFiles, currentFiles
        );

        Folder newFolder = mergedStagingArea.updateFolder(currentFolder);
        String newFolderSha = newFolder.generateSha();
        newFolder.saveToSha(newFolderSha);
        new WorkingDirectory().reset(newFolder, mergedStagingArea);

        Long timestamp = new Date().getTime();
        Commit newCommit = new Commit(
                "Merged " + givenBranch.name + " into " + name + ".",
                newFolderSha,
                currentCommit.getSha(),
                givenCommit.getSha(),
                timestamp
        );
        newCommit.save();

        setCommitSha(newCommit.getSha());
        save();
    }
}
