package gitlet;

import java.util.Objects;

/** Driver class for Gitlet, a subset of the Git version-control system.
 *  @author TODO
 */
public class Main {

    /** Usage: java gitlet.Main ARGS, where ARGS contains
     *  <COMMAND> <OPERAND1> <OPERAND2> ... 
     */
    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("Please enter a command.");
            return;
        }
        String firstArg = args[0];
        String filename;
        String message;
        String branchName;
        String commitSha;
        switch(firstArg) {
            case "init":
                validateNumArgs("init", args, 1);
                Repository.init();
                break;
            case "add":
                validateNumArgs("add", args, 2);
                filename = args[1];
                Repository.add(filename);
                break;
            case "rm":
                validateNumArgs("rm", args, 2);
                filename = args[1];
                Repository.rm(filename);
                break;
            case "commit":
                if (args.length != 2 || Objects.equals(args[1], "")) {
                    System.out.println("Please enter a commit message.");
                    return;
                }
                validateNumArgs("commit", args, 2);
                message = args[1];
                Repository.commit(message);
                break;
            case "log":
                validateNumArgs("log", args, 1);
                Repository.log();
                break;
            case "global-log":
                validateNumArgs("global-log", args, 1);
                Repository.globalLog();
                break;
            case "find":
                validateNumArgs("global-log", args, 2);
                message = args[1];
                Repository.find(message);
                break;
            case "status":
                validateNumArgs("status", args, 1);
                Repository.status();
                break;
            case "branch":
                validateNumArgs("branch", args, 2);
                branchName = args[1];
                Repository.branch(branchName);
                break;
            case "rm-branch":
                validateNumArgs("rm-branch", args, 2);
                branchName = args[1];
                Repository.removeBranch(branchName);
                break;
            case "reset":
                validateNumArgs("reset", args, 2);
                commitSha = args[1];
                Repository.reset(commitSha);
                break;
            case "merge":
                validateNumArgs("merge", args, 2);
                branchName = args[1];
                Repository.merge(branchName);
                break;
            case "checkout":
                switch(args.length) {
                    // checkout [branch name]
                    case 2:
                        validateNumArgs("checkout", args, 2);
                        branchName = args[1];
                        Repository.checkoutBranch(branchName);
                        break;
                    // checkout -- [file name]
                    case 3:
                        validateNumArgs("checkout", args, 3);
                        filename = args[2];
                        Repository.checkoutFileFromHead(filename);
                        break;
                    // checkout [commit id] -- [file name]
                    case 4:
                        validateNumArgs("checkout", args, 4);
                        commitSha = args[1];
                        filename = args[3];
                        Repository.checkoutFileFromCommit(commitSha, filename);
                        break;
                    default:
                        System.out.println("Invalid number of arguments to commit");
                        break;
                }
            default:
                System.out.println("No command with that name exists.");
                break;
        }
    }

    /**
     * Checks the number of arguments versus the expected number,
     * throws a RuntimeException if they do not match.
     *
     * @param cmd Name of command you are validating
     * @param args Argument array from command line
     * @param n Number of expected arguments
     */
    public static void validateNumArgs(String cmd, String[] args, int n) {
        if (args.length != n) {
            throw new RuntimeException(
                    String.format("Invalid number of arguments for: %s.", cmd));
        }
    }
}
