package deque;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class DequeTest {

    @Test
    public void testDequeEquality() {
        Deque<Integer> arrayDeque = new ArrayDeque<>();
        Deque<Integer> linkedListDeque = new LinkedListDeque<>();

        assertEquals(arrayDeque, linkedListDeque);

        int[] values = {1, 2, 3};

        for (int value: values) {
            arrayDeque.addLast(value);
            linkedListDeque.addLast(value);
        }

        assertEquals(arrayDeque, linkedListDeque);
        assertEquals(linkedListDeque, arrayDeque);
    }
}
