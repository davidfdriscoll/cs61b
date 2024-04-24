package deque;

import org.junit.Test;

import java.util.Iterator;

import static org.junit.Assert.*;


/** Performs some basic linked list tests. */
public class LinkedListDequeTest {

    @Test
    /** Adds a few things to the list, checking isEmpty() and size() are correct,
     * finally printing the results.
     *
     * && is the "and" operation. */
    public void addIsEmptySizeTest() {

        LinkedListDeque<String> lld1 = new LinkedListDeque<String>();

        assertTrue("A newly initialized LLDeque should be empty", lld1.isEmpty());
        lld1.addFirst("front");

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
    /** Adds an item, then removes an item, and ensures that dll is empty afterwards. */
    public void addRemoveTest() {

        LinkedListDeque<Integer> lld1 = new LinkedListDeque<Integer>();
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

        LinkedListDeque<Integer> lld1 = new LinkedListDeque<Integer>();
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

        LinkedListDeque<Integer> lld1 = new LinkedListDeque<>();
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
    /* Check if you can create LinkedListDeques with different parameterized types*/
    public void multipleParamTest() {

        LinkedListDeque<String>  lld1 = new LinkedListDeque<String>();
        LinkedListDeque<Double>  lld2 = new LinkedListDeque<Double>();
        LinkedListDeque<Boolean> lld3 = new LinkedListDeque<Boolean>();

        lld1.addFirst("string");
        lld2.addFirst(3.14159);
        lld3.addFirst(true);

        String s = lld1.removeFirst();
        double d = lld2.removeFirst();
        boolean b = lld3.removeFirst();
    }

    @Test
    /* check if null is return when removing from an empty LinkedListDeque. */
    public void emptyNullReturnTest() {

        System.out.println("Make sure to uncomment the lines below (and delete this print statement).");
        LinkedListDeque<Integer> lld1 = new LinkedListDeque<Integer>();

        boolean passed1 = false;
        boolean passed2 = false;
        assertEquals("Should return null when removeFirst is called on an empty Deque,", null, lld1.removeFirst());
        assertEquals("Should return null when removeLast is called on an empty Deque,", null, lld1.removeLast());

    }

    @Test
    /* Add large number of elements to deque; check if order is correct. */
    public void bigLLDequeTest() {

        LinkedListDeque<Integer> lld1 = new LinkedListDeque<Integer>();
        int runs = 1000000;
        for (int i = 0; i < runs; i++) {
            lld1.addLast(i);
        }

        int halfRuns = runs / 2;
        for (double i = 0; i < halfRuns; i++) {
            assertEquals("Should have the same value", i, (double) lld1.removeFirst(), 0.0);
        }

        for (double i = runs - 1; i > halfRuns; i--) {
            assertEquals("Should have the same value", i, (double) lld1.removeLast(), 0.0);
        }

    }

    @Test
    public void equalsTest() {
        LinkedListDeque<Integer> lld1 = new LinkedListDeque<Integer>();
        lld1.addLast(1);
        lld1.addLast(2);
        lld1.addLast(3);

        LinkedListDeque<Integer> lld2 = new LinkedListDeque<Integer>();
        lld2.addFirst(1);
        assertNotEquals(lld1, lld2);

        LinkedListDeque<Integer> lld3 = lld1;
        assertEquals(lld1, lld3);

        LinkedListDeque<Integer> lld4 = new LinkedListDeque<Integer>();
        lld4.addLast(1);
        lld4.addLast(2);
        lld4.addLast(3);
        assertEquals(lld1, lld4);

        LinkedListDeque<String> stringList = new LinkedListDeque<String>();
        stringList.addLast("1");
        stringList.addLast("2");
        stringList.addLast("3");
        assertNotEquals(lld1, stringList);
    }

    @Test
    public void iteratorTest() {
        LinkedListDeque<Integer> lld1 = new LinkedListDeque<>();
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
