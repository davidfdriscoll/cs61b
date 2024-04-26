package bstmap;

import java.util.*;

public class BSTMap<K extends Comparable,V> implements Map61B<K,V> {
    private class BSTNode<Y> {
        private final Y key;
        private V value;
        private BSTNode<Y> left;
        private BSTNode<Y> right;

        public BSTNode(Y key, BSTNode<Y> left, BSTNode<Y> right) {
            this.key = key;
            this.left = left;
            this.right = right;
        }

        public BSTNode(Y key) {
            this.key = key;
        }
    }

    BSTNode<K> root = null;
    int size = 0;

    @Override
    public void clear() {
        root = null;
        size = 0;
    }

    @Override
    public boolean containsKey(K key) {
        BSTNode<K> getNode = traverse(root, key, false);
        return getNode != null;
    }

    @Override
    public V get(K key) {
        BSTNode<K> getNode = traverse(root, key, false);
        if (getNode == null) {
            return null;
        }
        else {
            return getNode.value;
        }
    }

    @Override
    public int size() {
        return size;
    }

    private BSTNode<K> traverse(BSTNode<K> node, K key, boolean insert) {
        if (node == null) {
            if (insert) {
                return new BSTNode<>(key);
            } else {
                return null;
            }
        } else if (key.compareTo(node.key) == 0) {
            return node;
        } else if (key.compareTo(node.key) < 0) {
            node.left = traverse(node.left, key, insert);
            return node.left;
        } else {
            node.right = traverse(node.right, key, insert);
            return node.right;
        }
    }

    @Override
    public void put(K key, V value) {
        BSTNode<K> newNode = traverse(root, key, true);
        if (root == null) {
            root = newNode;
        }
        newNode.value = value;
        size++;
    }

    @Override
    public Set<K> keySet() {
        throw new UnsupportedOperationException();
    }

    @Override
    public V remove(K key) {
        throw new UnsupportedOperationException();
    }

    @Override
    public V remove(K key, V value) {
        return remove(key);
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
