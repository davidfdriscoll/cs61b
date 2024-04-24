package deque;

import org.junit.Test;

import java.util.Iterator;

import static org.junit.Assert.*;


/** Performs some basic linked list tests. */
public class ArrayDequeTest {

    @Test
    /** Adds a few things to the list, checking isEmpty() and size() are correct,
     * finally printing the results.
     *
     * && is the "and" operation. */
    public void addIsEmptySizeTest() {

        ArrayDeque<String> lld1 = new ArrayDeque<>();

        assertTrue("A newly initialized LLDeque should be empty", lld1.isEmpty());
        lld1.addFirst("front");

        // The && operator is the same as "and" in Python.
        // It's a binary operator that returns true if both arguments true, and false otherwise.
        assertEquals(1, lld1.size());
        assertFalse("lld1 should now contain 1 item", lld1.isEmpty());
        assertEquals("front", lld1.toString());

        lld1.addFirst("second");
        assertEquals(2, lld1.size());
        assertEquals("second front", lld1.toString());

        lld1.addLast("middle");
        assertEquals(3, lld1.size());
        assertEquals("second front middle", lld1.toString());

        lld1.addLast("back");
        assertEquals(4, lld1.size());
        assertEquals("second front middle back", lld1.toString());

        System.out.println("Printing out deque: ");
        lld1.printDeque();
    }

    @Test
    /** add eight items */
    public void addEight() {
        ArrayDeque<Integer> l = new ArrayDeque<>();
        for (int i = 1; i <= 8; i++) {
            l.addLast(i);
        }
        assertEquals(8, l.size());
        assertEquals("1 2 3 4 5 6 7 8", l.toString());
        for (int i = 0; i < 8; i++) {
            assertEquals(Integer.valueOf(i + 1), l.get(i));
        }
    }

    @Test
    /** add and remove a thousand items */
    public void addAndRemoveThousand() {
        ArrayDeque<Integer> l = new ArrayDeque<>();
        for (int i = 1; i <= 1000; i++) {
            l.addLast(0);
            l.addFirst(0);
        }
        for (int i = 1; i <= 1000; i++) {
            l.removeFirst();
            l.removeLast();
        }
        assertEquals(0, l.size());
        assertNull(l.get(0));

        l.addLast(0);
        assertEquals(1, l.size());
        assertEquals(Integer.valueOf(0), l.get(0));
    }

    @Test
    /** add two thousand items */
    public void addTwoThousand() {
        ArrayDeque<Integer> l = new ArrayDeque<>();
        l.addFirst(0);
        for (int i = 1; i <= 1000; i++) {
            l.addLast(i);
            l.addFirst(-1 * i);
        }
        assertEquals(2001, l.size());
        int expected = -1000;
        for (int i = 0; i < 2001; i++) {
            assertEquals(Integer.valueOf(expected), l.get(i));
            expected++;
        }
    }

    @Test
    /** Adds an item, then removes an item, and ensures that dll is empty afterwards. */
    public void addRemoveTest() {

        ArrayDeque<Integer> lld1 = new ArrayDeque<Integer>();
        // should be empty
        assertTrue("lld1 should be empty upon initialization", lld1.isEmpty());
        assertNull(lld1.removeFirst());

        lld1.addFirst(10);
        // should not be empty
        assertFalse("lld1 should contain 1 item", lld1.isEmpty());
        assertEquals("10", lld1.toString());

        int removed = lld1.removeFirst();
        // should be empty
        assertTrue("lld1 should be empty after removal", lld1.isEmpty());
        assertEquals(10, removed);
        assertNull(lld1.removeFirst());
    }

    @Test
    /** Adds two items, then removes last, and ensures dll has expected item. */
    public void removeLastTest() {

        ArrayDeque<Integer> lld1 = new ArrayDeque<Integer>();
        // should be empty
        assertTrue("lld1 should be empty upon initialization", lld1.isEmpty());

        lld1.addLast(2);
        lld1.addFirst(1);
        assertEquals("1 2", lld1.toString());
        assertEquals(Integer.valueOf(1), lld1.get(0));
        assertEquals(Integer.valueOf(2), lld1.get(1));
        assertNull(lld1.get(2));

        int removed = lld1.removeLast();
        assertEquals(2, removed);
        assertEquals("1", lld1.toString());
        assertEquals(Integer.valueOf(1), lld1.get(0));
        assertNull(lld1.get(1));
    }

    @Test
    /* Tests removing from an empty deque */
    public void removeEmptyTest() {

        ArrayDeque<Integer> lld1 = new ArrayDeque<>();
        lld1.addFirst(3);

        lld1.removeLast();
        lld1.removeFirst();
        lld1.removeLast();
        lld1.removeFirst();

        int size = lld1.size();
        String errorMsg = "  Bad size returned when removing from empty deque.\n";
        errorMsg += "  student size() returned " + size + "\n";
        errorMsg += "  actual size() returned 0\n";

        assertEquals(errorMsg, 0, size);
    }

    @Test
    /* Check if you can create ArrayDeques with different parameterized types*/
    public void multipleParamTest() {

        ArrayDeque<String>  lld1 = new ArrayDeque<String>();
        ArrayDeque<Double>  lld2 = new ArrayDeque<Double>();
        ArrayDeque<Boolean> lld3 = new ArrayDeque<Boolean>();

        lld1.addFirst("string");
        lld2.addFirst(3.14159);
        lld3.addFirst(true);

        String s = lld1.removeFirst();
        double d = lld2.removeFirst();
        boolean b = lld3.removeFirst();
    }

    @Test
    /* check if null is return when removing from an empty ArrayDeque. */
    public void emptyNullReturnTest() {

        System.out.println("Make sure to uncomment the lines below (and delete this print statement).");
        ArrayDeque<Integer> lld1 = new ArrayDeque<Integer>();

        boolean passed1 = false;
        boolean passed2 = false;
        assertEquals("Should return null when removeFirst is called on an empty Deque,", null, lld1.removeFirst());
        assertEquals("Should return null when removeLast is called on an empty Deque,", null, lld1.removeLast());

    }

    @Test
    /* Add large number of elements to deque; check if order is correct. */
    public void bigLLDequeTest() {

        ArrayDeque<Integer> lld1 = new ArrayDeque<Integer>();
        for (int i = 0; i < 1000000; i++) {
            lld1.addLast(i);
        }

        for (double i = 0; i < 500000; i++) {
            assertEquals("Should have the same value", i, (double) lld1.removeFirst(), 0.0);
        }

        for (double i = 999999; i > 500000; i--) {
            assertEquals("Should have the same value", i, (double) lld1.removeLast(), 0.0);
        }

    }

    @Test
    public void equalsTest() {
        ArrayDeque<Integer> lld1 = new ArrayDeque<Integer>();
        lld1.addLast(1);
        lld1.addLast(2);
        lld1.addLast(3);

        ArrayDeque<Integer> lld2 = new ArrayDeque<Integer>();
        lld2.addFirst(1);
        assertNotEquals(lld1, lld2);

        ArrayDeque<Integer> lld3 = lld1;
        assertEquals(lld1, lld3);

        ArrayDeque<Integer> lld4 = new ArrayDeque<Integer>();
        lld4.addLast(1);
        lld4.addLast(2);
        lld4.addLast(3);
        assertEquals(lld1, lld4);

        ArrayDeque<String> stringList = new ArrayDeque<String>();
        stringList.addLast("1");
        stringList.addLast("2");
        stringList.addLast("3");
        assertNotEquals(lld1, stringList);
    }

    @Test
    public void iteratorTest() {
        ArrayDeque<Integer> lld1 = new ArrayDeque<>();
        lld1.addLast(1);
        lld1.addLast(2);
        lld1.addLast(3);

        Iterator<Integer> iterator = lld1.iterator();

        Integer i = 1;
        while (iterator.hasNext()) {
            assertEquals(i, iterator.next());
            i += 1;
        }
    }
}
