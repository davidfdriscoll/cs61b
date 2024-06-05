package hashmap;

import java.util.*;

/**
 *  A hash table-backed Map implementation. Provides amortized constant time
 *  access to elements via get(), remove(), and put() in the best case.
 *
 *  Assumes null keys will never be inserted, and does not resize down upon remove().
 *  @author YOUR NAME HERE
 */
public class MyHashMap<K, V> implements Map61B<K, V> {

    @Override
    public void clear() {
        buckets = createTable(buckets.length);
        size = 0;
    }

    @Override
    public boolean containsKey(K key) {
        return get(key) != null;
    }

    private Node getNode(K key) {
        int bucketIdx = bucketIdx(buckets, key);
        Collection<Node> bucket = buckets[bucketIdx];
        for (Node node: bucket) {
            if (node.key.equals(key)) {
                return node;
            }
        }
        return null;
    }

    @Override
    public V get(K key) {
        Node node = getNode(key);
        if (node == null) {
            return null;
        }
        return node.value;
    }

    @Override
    public int size() {
        return size;
    }

    private int bucketIdx(Collection<Node>[] localBuckets, K key) {
        int hash = key.hashCode();
        return Math.abs(hash) % localBuckets.length;
    }

    private void resize(int newSize) {
        Collection<Node>[] newBuckets = createTable(newSize);
        for (Node node: nodeSet()) {
            int bucketIdx = bucketIdx(newBuckets, node.key);
            newBuckets[bucketIdx].add(node);
        }
        buckets = newBuckets;
    }

    @Override
    public void put(K key, V value) {
        if ((double) size / buckets.length > maxLoad) {
            resize(buckets.length * 2);
        }
        Node node = createNode(key, value);
        Node existingNode = getNode(node.key);
        if (existingNode != null) {
            existingNode.value = node.value;
        } else {
            int bucketIdx = bucketIdx(buckets, node.key);
            buckets[bucketIdx].add(node);
            size++;
        }
    }

    private Set<Node> nodeSet() {
        Set<Node> set = new HashSet<>();
        for (Collection<Node> bucket: buckets) {
            set.addAll(bucket);
        }
        return set;
    }

    @Override
    public Set<K> keySet() {
        Set<K> set = new HashSet<>();
        for (Node node: nodeSet()) {
            set.add(node.key);
        }
        return set;
    }

    @Override
    public V remove(K key) {
        Node node = getNode(key);
        int bucketIdx = bucketIdx(buckets, key);
        Collection<Node> bucket = buckets[bucketIdx];
        bucket.remove(node);
        if (node == null) {
            return null;
        }
        return node.value;
    }

    @Override
    public V remove(K key, V value) {
        return remove(key);
    }

    @Override
    public Iterator<K> iterator() {
        return keySet().iterator();
    }

    /**
     * Protected helper class to store key/value pairs
     * The protected qualifier allows subclass access
     */
    protected class Node {
        K key;
        V value;

        Node(K k, V v) {
            key = k;
            value = v;
        }
    }

    /* Instance Variables */
    private static final Integer defaultSize = 16;
    private static final Double defaultLoad = 0.75;
    private Collection<Node>[] buckets;
    private int size = 0;
    private final double maxLoad;

    /** Constructors */
    public MyHashMap() {
        this(defaultSize, defaultLoad);
    }

    public MyHashMap(int initialSize) {
        this(initialSize, defaultLoad);
    }

    /**
     * MyHashMap constructor that creates a backing array of initialSize.
     * The load factor (# items / # buckets) should always be <= loadFactor
     *
     * @param initialSize initial size of backing array
     * @param maxLoad maximum load factor
     */
    public MyHashMap(int initialSize, double maxLoad) {
        buckets = createTable(initialSize);
        this.maxLoad = maxLoad;
    }

    /**
     * Returns a new node to be placed in a hash table bucket
     */
    private Node createNode(K key, V value) {
        return new Node(key, value);
    }

    /**
     * Returns a data structure to be a hash table bucket
     *
     * The only requirements of a hash table bucket are that we can:
     *  1. Insert items (`add` method)
     *  2. Remove items (`remove` method)
     *  3. Iterate through items (`iterator` method)
     *
     * Each of these methods is supported by java.util.Collection,
     * Most data structures in Java inherit from Collection, so we
     * can use almost any data structure as our buckets.
     *
     * Override this method to use different data structures as
     * the underlying bucket type
     *
     * BE SURE TO CALL THIS FACTORY METHOD INSTEAD OF CREATING YOUR
     * OWN BUCKET DATA STRUCTURES WITH THE NEW OPERATOR!
     */
    protected Collection<Node> createBucket() {
        return new LinkedList<>();
    }

    /**
     * Returns a table to back our hash table. As per the comment
     * above, this table can be an array of Collection objects
     *
     * BE SURE TO CALL THIS FACTORY METHOD WHEN CREATING A TABLE SO
     * THAT ALL BUCKET TYPES ARE OF JAVA.UTIL.COLLECTION
     *
     * @param tableSize the size of the table to create
     */
    private Collection<Node>[] createTable(int tableSize) {
        Collection[] table = new Collection[tableSize];
        for (int i = 0; i < tableSize; i++) {
            table[i] = createBucket();
        }
        return table;
    }
}
