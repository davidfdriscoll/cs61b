package randomizedtest;

import edu.princeton.cs.introcs.StdRandom;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Created by hug.
 */
public class TestBuggyAList {
  @Test
  public void testThreeAddThreeRemove() {
      AListNoResizing<Integer> goodList = new AListNoResizing<Integer>();
      BuggyAList<Integer> badList = new BuggyAList<Integer>();

      int[] nums = {1, 2, 3};
      for (int num: nums) {
          goodList.addLast(num);
          badList.addLast(num);
      }

      for (int ignored : nums) {
         assertEquals(goodList.removeLast(), badList.removeLast());
      }
  }

    @Test
    public void testAddRemove1000() {
        AListNoResizing<Integer> goodList = new AListNoResizing<Integer>();
        BuggyAList<Integer> badList = new BuggyAList<Integer>();

        int count = 1000;
        for (int i = 1; i <= count; i++) {
            goodList.addLast(i);
            badList.addLast(i);
        }

        for (int i = 1; i <= count; i++) {
            assertEquals(goodList.removeLast(), badList.removeLast());
        }
    }

  @Test
    public void randomizedTest() {
      AListNoResizing<Integer> L = new AListNoResizing<>();
      BuggyAList<Integer> badList = new BuggyAList<Integer>();

      int N = 50000;
      for (int i = 0; i < N; i += 1) {
          int operationNumber = StdRandom.uniform(0, 3);
          if (operationNumber == 0) {
              // addLast
              int randVal = StdRandom.uniform(0, 100);
              L.addLast(randVal);
              badList.addLast(randVal);
          } else if (operationNumber == 1) {
              // size
              int goodSize = L.size();
              int badSize = badList.size();
              if (goodSize != badSize) {
                  System.out.println("goodSize: " + goodSize + " badSize: " + badSize);
              }
          } else if (operationNumber == 2) {
              // getLast and removeLast
              int goodSize = L.size();
              if (goodSize > 0) {
                  int goodGetLast = L.getLast();
                  int badGetLast = badList.getLast();
                  if (goodGetLast != badGetLast) {
                      System.out.println("goodLast: " + goodGetLast + " badGetLast: " + badGetLast);
                  }

                  int goodRemoveLast = L.removeLast();
                  int badRemoveLast = badList.removeLast();
                  if (goodRemoveLast != badRemoveLast) {
                      System.out.println("goodRemoveLast: " + goodRemoveLast + " badRemoveLast: " + badRemoveLast);
                  }
              }
          }
      }
  }
}
