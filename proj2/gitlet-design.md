# Gitlet Design Document

**Name**: David Driscoll

## Classes and Data Structures

### Directory structure

```
.gitlet
  HEAD
  objects
    commits
    files
    folders
  refs
    heads
```

### File

#### Fields

1. String name
2. byte[] contents


### Folder

#### Fields

1. Map<String filename, String sha> files

### Commit

#### Fields

1. String folderSha 
2. String parentSha
3. String mergeParentSha
4. Long timestamp


## Algorithms

## Persistence

