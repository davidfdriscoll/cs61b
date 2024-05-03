package gitlet;

/** Driver class for Gitlet, a subset of the Git version-control system.
 *  @author TODO
 */
public class Main {

    /** Usage: java gitlet.Main ARGS, where ARGS contains
     *  <COMMAND> <OPERAND1> <OPERAND2> ... 
     */
    public static void main(String[] args) {
        // TODO: what if args is empty?
        String firstArg = args[0];
        String filename;
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
            case "commit":
                if (args.length < 2) {
                    System.out.println("Please enter a commit message.");
                    return;
                }
                validateNumArgs("commit", args, 2);
                String message = args[1];
                Repository.commit(message);
                break;
            case "log":
                validateNumArgs("log", args, 1);
                Repository.log();
                break;
            case "checkout":
                String secondArg = args[1];
                switch(secondArg) {
                    // checkout -- [file name]
                    case "--":
                        validateNumArgs("checkout", args, 3);
                        filename = args[2];
                        Repository.checkoutFileFromHead(filename);
                        break;
                    // checkout [commit id] -- [file name]
                    default:
                        validateNumArgs("checkout", args, 4);
                        String commitSha = args[1];
                        filename = args[3];
                        Repository.checkoutFileFromCommit(commitSha, filename);
                        break;
                }

            // debug methods
            case "debug":
                break;
            // TODO: FILL THE REST IN
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
