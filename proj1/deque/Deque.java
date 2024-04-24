package deque;

import java.util.Iterator;

public interface Deque<T> {

    /**
     * Adds an item of type T to the front of the deque.
     * You can assume that item is never null.
     * @param item item to add
     */
    void addFirst(T item);

    /**
     * Adds an item of type T to the back of the deque.
     * You can assume that item is never null.
     * @param item item to add
     */
    void addLast(T item);

    /**
     * @return true if deque is empty, false otherwise.
     */
    default boolean isEmpty() {
        return size() == 0;
    }

    /**
     * Returns the number of items in the deque.
     * @return size of deque
     */
    int size();


    /** Method to return a string representation of an Deque */
    String toString();

    /**
     * Prints the items in the deque from first to last, separated by a space.
     * Once all the items have been printed, print out a new line.
     */
    void printDeque();

    /**
     * Removes and returns the item at the front of the deque.
     * If no such item exists, returns null.
     * @return first item in deque
     */
    T removeFirst();

    /**
     * Removes and returns the item at the back of the deque.
     * If no such item exists, returns null.
     * @return last item in deque
     */
    T removeLast();

    /**
     * Gets the item at the given index, where 0 is the front, 1 is the next item, and so forth.
     * If no such item exists, returns null. Must not alter the deque!
     * @param index index of value to be returned
     * @return value at desired index
     */
    T get(int index);

    /**
     * The Deque objects we’ll make are iterable (i.e. Iterable<T>)
     * so we must provide this method to return an iterator.
     * @return iterator
     */
    Iterator<T> iterator();

    /**
     * Returns whether the parameter o is equal to the Deque.
     * o is considered equal if it is a Deque and
     * if it contains the same contents (as governed by
     * the generic T’s equals method) in the same order.
     * (ADDED 2/12: You’ll need to use the instance of keywords for this.
     * Read here for more information)
     * @param o object to be considered
     * @return whether this deque is equal to the passed in object
     */
    boolean equals(Object o);
}
