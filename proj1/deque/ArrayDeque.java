package deque;

import java.util.Iterator;

public class ArrayDeque<T> implements Iterable<T>, Deque<T> {
    private T[] items;
    private int size;
    private int nextFirst;
    private int nextLast;

    private static final int MULTIPLIER = 2;
    private static final int DIVIDER = 4;


    /** Creates an empty list. */
    public ArrayDeque() {
        items = (T[]) new Object[8];
        size = 0;
        nextFirst = 4;
        nextLast = 5;
    }

    /** Resizes the underlying array to the target capacity. */
    private void resize(int capacity) {
        T[] newItems = (T[]) new Object[capacity];
        int oldIdx = incIdx(nextFirst);
        for (int newIdx = 1; newIdx <= size; newIdx++) {
            newItems[newIdx] = items[oldIdx];
            oldIdx = incIdx(oldIdx);
        }
        items = newItems;
        nextFirst = 0;
        nextLast = size + 1;
    }

    private int incIdx(int idx) {
        if (idx == items.length - 1) {
            return 0;
        } else {
            return idx + 1;
        }
    }

    private int decIdx(int idx) {
        if (idx == 0) {
            return items.length - 1;
        } else {
            return idx - 1;
        }
    }

    @Override
    public void addFirst(T item) {
        if (size == items.length) {
            resize(items.length * MULTIPLIER);
        }
        size++;
        items[nextFirst] = item;
        nextFirst = decIdx(nextFirst);
    }

    @Override
    public void addLast(T item) {
        if (size == items.length) {
            resize(items.length * MULTIPLIER);
        }
        size++;
        items[nextLast] = item;
        nextLast = incIdx(nextLast);
    }

    @Override
    public int size() {
        return size;
    }

    private String makeString() {
        if (size == 0) {
            return "";
        }
        String str = items[nextFirst + 1].toString();
        int idx = nextFirst + 2;
        while (idx != nextLast) {
            str = str + " " + items[idx];
            idx = incIdx(idx);
        }
        return str;
    }

    @Override
    public void printDeque() {
        System.out.println(makeString());
    }

    @Override
    public T removeFirst() {
        if (size == 0) {
            return null;
        }
        if ((size < items.length / DIVIDER) && (size > DIVIDER)) {
            resize(items.length / DIVIDER);
        }
        size--;
        nextFirst = incIdx(nextFirst);
        T removed = items[nextFirst];
        items[nextFirst] = null;
        return removed;
    }

    @Override
    public T removeLast() {
        if (size == 0) {
            return null;
        }
        if ((size < items.length / DIVIDER) && (size > DIVIDER)) {
            resize(items.length / DIVIDER);
        }
        size--;
        nextLast = decIdx(nextLast);
        T removed = items[nextLast];
        items[nextLast] = null;
        return removed;
    }

    @Override
    public T get(int index) {
        return items[(nextFirst + index + 1) % items.length];
    }

    private class ArrayDequeIterator implements Iterator<T> {
        private int pos;

        ArrayDequeIterator() {
            pos = 0;
        }

        public boolean hasNext() {
            return pos < size;
        }

        public T next() {
            T returnItem = get(pos);
            pos += 1;
            return returnItem;
        }
    }

    public Iterator<T> iterator() {
        return new ArrayDequeIterator();
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Deque)) {
            return false;
        }
        // cast, god forgive us
        Deque<T> castO = (Deque<T>) o;
        if (size() != castO.size()) {
            return false;
        }
        for (int i = 0; i < size(); i++) {
            if (!get(i).equals(castO.get(i))) {
                return false;
            }
        }
        return true;
    }
}
