package datastructures;

import org.jetbrains.annotations.Nullable;

import java.util.LinkedList;
import java.util.List;

public class HashMap<K, V> {
    private int capacity;
    private Bucket<K, V>[] buckets;

    final private double loadFactor;
    private int size = 0;
    private int numberOfNodes = 0;

    public HashMap(int capacity) {
        this.capacity = capacity;
        buckets = new Bucket[capacity];
        loadFactor = 0.75;
    }

    public void addEntry(K key, V value) {
        numberOfNodes++;
        int hash = hashCode(key);
        if (buckets[hash] == null) {
            size++;
            buckets[hash] = new Bucket();
        }
        buckets[hash].addEntry(new Entry(key, value));
        if (shouldExpand()) expand();
    }

    public void removeEntry(K key) {
        int hash = hashCode(key);
        if (buckets[hash] != null) {
            if (buckets[hash].removeEntry(key)) {
                numberOfNodes--;
                if (buckets[hash].getEntries().isEmpty()) {
                    buckets[hash] = null;
                    size--;
                }
            }
        }
    }

    @Nullable
    public V getValue(K key) {
        Bucket<K, V> bucket = buckets[hashCode(key)];
        if (bucket != null) {
            Entry<K, V> entry = bucket.getEntry(key);
            if (entry != null) return entry.getValue();
        }
        return null;
    }

    public LinkedList<V> getAllNodes() {
        LinkedList<V> allNodes = new LinkedList<>();
        for (Bucket<K, V> bucket: buckets) {
            if (bucket != null) {
                for (Entry<K, V> entry: bucket.getEntries()) {
                    allNodes.add(entry.getValue());
                }
            }
        }
        return allNodes;
    }

    public boolean isEmpty() { return getAllNodes().isEmpty(); }

    private boolean shouldExpand() { return ((double) size / capacity >= loadFactor); }

    private void expand() {
        capacity = capacity*2;
        Bucket<K, V>[] oldBuckets = this.buckets;
        buckets = new Bucket[capacity];

        for (Bucket<K, V> oldBucket : oldBuckets) {
            if (oldBucket != null) {
                for (Entry<K, V> entry : oldBucket.getEntries()) {
                    int hash = hashCode(entry.key);
                    if (buckets[hash] == null) buckets[hash] = new Bucket();
                    buckets[hash].addEntry(entry);
                }
            }
        }
    }

    public int hashCode(K key) {
        return key.hashCode() % this.capacity;
    }

    public int getNumberOfNodes() { return numberOfNodes; }
}

class Entry<K, V> {
    public K key;
    public V value;

    public Entry(K key, V value) {
        this.key = key;
        this.value = value;
    }

    public V getValue() { return value; }
}

class Bucket<K, V> {
    private LinkedList<Entry<K, V>> entries;

    public void addEntry(Entry<K, V> entry){
        if (entries == null) entries = new LinkedList<>();
        entries.add(entry);
    }

    public Entry<K, V> getEntry(K key) {
        for (Entry<K, V> entry : entries) {
            if (entry.key.equals(key)) return entry;
        }
        return null;
    }

    public boolean removeEntry(K key) {
        for (int i = 0; i < entries.size(); i++) {
            if (entries.get(i).key.equals(key)) {
                entries.remove(i);
                return true;
            }
        }
        return false;
    }

    public List<Entry<K, V>> getEntries() { return entries; }
}
