package timingtest;
import edu.princeton.cs.algs4.Stopwatch;

/**
 * Created by hug.
 */
public class TimeAList {
    private static void printTimingTable(AList<Integer> Ns, AList<Double> times, AList<Integer> opCounts) {
        System.out.printf("%12s %12s %12s %12s\n", "N", "time (s)", "# ops", "microsec/op");
        System.out.printf("------------------------------------------------------------\n");
        for (int i = 0; i < Ns.size(); i += 1) {
            int N = Ns.get(i);
            double time = times.get(i);
            int opCount = opCounts.get(i);
            double timePerOp = time / opCount * 1e6;
            System.out.printf("%12d %12.2f %12d %12.2f\n", N, time, opCount, timePerOp);
        }
    }

    public static void main(String[] args) {
        timeAListConstruction();
    }

    public static AList<Integer> createNs() {
        AList<Integer> Ns = new AList<Integer>();
        int count = 1000;
        for (int i = 0; i < 8; i++) {
            Ns.addLast(count);
            count *= 2;
        }
        return Ns;
    }

    public static void timeAListConstruction() {
        AList<Integer> Ns = new AList<Integer>();
        AList<Double> times = new AList<Double>();
        AList<Integer> opCounts = new AList<Integer>();

        int count = 1000;
        int doublings = 8;
        for (int i = 0; i < doublings; i++) {
            Ns.addLast(count);
            opCounts.addLast(count);

            AList<Integer> temp = new AList<Integer>();
            Stopwatch sw = new Stopwatch();
            for (int j = 0; j < count; j++) {
                temp.addLast(j);
            }
            times.addLast(sw.elapsedTime());

            count *= 2;
        }

        printTimingTable(Ns, times, opCounts);
    }
}
