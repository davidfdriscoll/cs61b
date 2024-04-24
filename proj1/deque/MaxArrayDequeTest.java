package deque;

import org.junit.Test;

import java.util.Comparator;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class MaxArrayDequeTest {

    @Test
    public void testEmptyDeque() {
        Comparator<Integer> comparator = Comparator.naturalOrder();
        MaxArrayDeque<Integer> deque = new MaxArrayDeque<>(comparator);
        assertNull(deque.max());
    }

    @Test
    public void testSimpleDeque() {
        Comparator<Integer> comparator = Comparator.naturalOrder();
        MaxArrayDeque<Integer> deque = new MaxArrayDeque<>(comparator);
        deque.addFirst(3);
        deque.addFirst(2);
        deque.addFirst(1);
        assertEquals(Integer.valueOf(3), deque.max());
    }

    @Test
    public void testSimpleDequeMin() {
        Comparator<Integer> comparator = Comparator.reverseOrder();
        MaxArrayDeque<Integer> deque = new MaxArrayDeque<>(comparator);
        deque.addFirst(3);
        deque.addFirst(2);
        deque.addFirst(1);
        assertEquals(Integer.valueOf(1), deque.max());
    }
}
