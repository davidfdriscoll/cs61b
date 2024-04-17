package deque;

import java.util.Objects;

public class LinkedListDeque<T> implements Deque<T> {
    private final Node sentinel;
    private int size;

    public class Node {
        public Node prev;
        public T item;
        public Node next;

        public Node(T i, Node p, Node n) {
            item = i;
            prev = p;
            next = n;
        }
    }

    public LinkedListDeque() {
        size = 0;
        sentinel = new Node(null, null, null);
        sentinel.next = sentinel;
        sentinel.prev = sentinel;
    }

    @Override
    public void addFirst(T item) {
        Node newNode = new Node(item, null, null);
        newNode.next = sentinel.next;
        newNode.prev = sentinel;
        sentinel.next = newNode;
        newNode.next.prev = newNode;
        size += 1;
    }

    @Override
    public void addLast(T item) {
        Node newNode = new Node(item, null, null);
        newNode.prev = sentinel.prev;
        newNode.next = sentinel;
        sentinel.prev = newNode;
        newNode.prev.next = newNode;
        size += 1;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    @Override
    public int size() {
        return size;
    }

    // construct a string of the list beginning with this pointer
    private String toStringHelper(Node pointer) {
        if (pointer == sentinel) return "";
        String rest = toStringHelper(pointer.next);
        if (Objects.equals(rest, "")) {
            return pointer.item.toString();
        } else {
            return pointer.item.toString() + " " + rest;
        }
    }

    public String toString() {
        return toStringHelper(sentinel.next);
    }

    @Override
    public void printDeque() {
        System.out.println(toString());
    }

    @Override
    public T removeFirst() {
        if (size == 0) return null;
        size -= 1;
        Node removedNode = sentinel.next;
        sentinel.next = removedNode.next;
        removedNode.next.prev = sentinel;
        return removedNode.item;
    }

    @Override
    public T removeLast() {
        if (size == 0) return null;
        size -= 1;
        Node removedNode = sentinel.prev;
        sentinel.prev = removedNode.prev;
        removedNode.prev.next = sentinel;
        return removedNode.item;
    }

    @Override
    public T get(int index) {
        if (size == 0 || index >= size) return null;
        Node pointer = sentinel.next;
        while (index > 0) {
            pointer = pointer.next;
            index -= 1;
        }
        return pointer.item;
    }

    public boolean equals(Object o) {
        if (!(o instanceof LinkedListDeque)) return false;
        // cast, god forgive us
        LinkedListDeque<T> castO = (LinkedListDeque<T>) o;
        if (size != castO.size()) return false;
        for (int i = 0; i < size; i++) {
            if (get(i) != castO.get(i)) return false;
        }
        return true;
    }
}
