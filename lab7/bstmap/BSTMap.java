package bstmap;

import java.util.*;

public class BSTMap<K extends Comparable<K>,V> implements Map61B<K,V> {
    private class BSTNode<Y> {
        private Y key;
        private V value;
        private BSTNode<Y> left;
        private BSTNode<Y> right;

        public BSTNode(Y key, V value, BSTNode<Y> left, BSTNode<Y> right) {
            this.key = key;
            this.value = value;
            this.left = left;
            this.right = right;
        }

        public BSTNode(Y key) {
            this.key = key;
        }
    }

    private BSTNode<K> root = null;
    private int size = 0;

    private void printInOrderHelper(BSTNode<K> node) {
        if (node == null) {
            return;
        }
        printInOrderHelper(node.left);
        System.out.print(node.key + " ");
        printInOrderHelper(node.right);
    }

    public void printInOrder() {
        printInOrderHelper(root);
    }

    @Override
    public void clear() {
        root = null;
        size = 0;
    }

    @Override
    public boolean containsKey(K key) {
        BSTNode<K> node = getHelper(root, key);
        return node != null;
    }

    private BSTNode<K> getHelper(BSTNode<K> node, K key) {
        if (node == null) {
            return null;
        }
        if (key.compareTo(node.key) == 0) {
            return node;
        } else if (key.compareTo(node.key) < 0) {
            return getHelper(node.left, key);
        } else {
            return getHelper(node.right, key);
        }
    }

    @Override
    public V get(K key) {
        BSTNode<K> node = getHelper(root, key);
        if (node == null) {
            return null;
        }
        return node.value;
    }

    @Override
    public int size() {
        return size;
    }

    private BSTNode<K> putHelper(BSTNode<K> node, K key, V value) {
        if (node == null) {
            return new BSTNode<K>(key, value, null, null);
        }
        int cmp = key.compareTo(node.key);
        if (cmp == 0) {
            node.key = key;
            node.value = value;
        } else if (cmp < 0) {
            node.left = putHelper(node.left, key, value);
        } else {
            node.right = putHelper(node.right, key, value);
        }
        return node;
    }

    @Override
    public void put(K key, V value) {
        root = putHelper(root, key, value);
        size++;
    }

    @Override
    public Set<K> keySet() {
        final Deque<BSTNode<K>> queue = new LinkedList<>();
        final Set<K> set = new HashSet<>();

        queue.addLast(root);
        while (!queue.isEmpty()) {
            BSTNode<K> node = queue.removeFirst();
            if (node == null) {
                continue;
            }
            set.add(node.key);
            queue.addLast(node.left);
            queue.addLast(node.right);
        }

        return set;
    }

    private BSTNode<K> findMax(BSTNode<K> node) {
        if (node.right == null) {
            return node;
        } else {
            return findMax(node.right);
        }
    }

    private BSTNode<K> removeHelper(BSTNode<K> node, K key) {
        int cmp = key.compareTo(node.key);
        if (cmp < 0) {
            node.left = removeHelper(node.left, key);
            return node;
        }
        if (cmp > 0) {
            node.right = removeHelper(node.right, key);
            return node;
        }

        // has no children
        if (node.left == null && node.right == null) {
            node = null;
            return node;
        } else if (node.left != null && node.right == null) {
        // has only left
            node = node.left;
            return node;
        // has only right
        } else if (node.right != null && node.left == null) {
            node = node.right;
            return node;
        }
        // has both children
        else {
            BSTNode<K> successor = findMax(node.left);
            successor.right = node.right;
            node = successor;
            return node;
        }
    }

    @Override
    public V remove(K key) {
        V value = get(key);
        return remove(key, value);
    }

    @Override
    public V remove(K key, V value) {
        V valueFromMap = get(key);
        if (valueFromMap != value) {
            throw new InputMismatchException();
        }
        root = removeHelper(root, key);
        size--;
        return value;
    }

    private class BSTMapIterator implements Iterator<K> {
        private final Deque<BSTNode<K>> queue = new LinkedList<>();

        BSTMapIterator(BSTNode<K> root) {
            if (root != null) {
                this.queue.addLast(root);
            }
        }

        public boolean hasNext() {
            return !queue.isEmpty();
        }

        public K next() {
            BSTNode<K> here = queue.removeFirst();
            if (here.left != null) {
                queue.addLast(here.left);
            }
            if (here.right != null) {
                queue.addLast(here.right);
            }
            return here.key;
        }
    }

    @Override
    public Iterator<K> iterator() {
        return new BSTMapIterator(root);
    }
}
