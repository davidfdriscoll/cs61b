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
2. String sha


### Folder

#### Fields

1. Map<String filename, String sha> files
2. String sha

### Commit

#### Fields

1. String sha
2. Folder folder 
3. Commit parent
4. Commit mergeParent
5. Date timestamp


## Algorithms

## Persistence

