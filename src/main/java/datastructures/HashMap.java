package datastructures;

import java.util.LinkedList;
import java.util.List;

public class HashMap<T, V> {
    private int capacity;
    private Bucket<T, V>[] buckets;

    final private double loadFactor;
    private int size = 0;
    private int numberOfNodes = 0;

    public HashMap(int capacity) {
        this.capacity = capacity;
        buckets = new Bucket[capacity];
        loadFactor = 0.75;
    }

    public void addEntry(T key, V value) {
        numberOfNodes++;
        int hash = hashCode(key);
        if (buckets[hash] == null) {
            size++;
            buckets[hash] = new Bucket();
        }
        buckets[hash].addEntry(new Entry(key, value));
        if (shouldExpand()) expand();
    }

    public void removeEntry(T key) {
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

    public V getValue(T key) {
        Bucket<T, V> bucket = buckets[hashCode(key)];
        if (bucket != null) {
            Entry<T, V> entry = bucket.getEntry(key);
            if (entry != null) return entry.getValue();
        }
        return null;
    }

    public LinkedList<V> getAllNodes() {
        LinkedList<V> allNodes = new LinkedList<>();
        for (Bucket<T, V> bucket: buckets) {
            if (bucket != null) {
                for (Entry<T, V> entry: bucket.getEntries()) {
                    allNodes.add(entry.getValue());
                }
            }
        }
        return allNodes;
    }

    private boolean shouldExpand() { return ((double) size / capacity >= loadFactor); }

    private void expand() {
        capacity = capacity*2;
        Bucket<T, V>[] oldBuckets = this.buckets;
        buckets = new Bucket[capacity];

        for (Bucket<T, V> oldBucket : oldBuckets) {
            if (oldBucket != null) {
                for (Entry<T, V> entry : oldBucket.getEntries()) {
                    int hash = hashCode(entry.key);
                    if (buckets[hash] == null) buckets[hash] = new Bucket();
                    buckets[hash].addEntry(entry);
                }
            }
        }
    }

    public int hashCode(T key) {
        return key.hashCode() % this.capacity;
    }

    public int getNumberOfNodes() { return numberOfNodes; }
}

class Entry<T, V> {
    public T key;
    public V value;

    public Entry(T key, V value) {
        this.key = key;
        this.value = value;
    }

    public V getValue() { return value; }
}

class Bucket<T, V> {
    private LinkedList<Entry<T, V>> entries;

    public void addEntry(Entry<T, V> entry){
        if (entries == null) entries = new LinkedList<>();
        entries.add(entry);
    }

    public Entry<T, V> getEntry(T key) {
        for (Entry<T, V> entry : entries) {
            if (entry.key.equals(key)) return entry;
        }
        return null;
    }

    public boolean removeEntry(T key) {
        for (int i = 0; i < entries.size(); i++) {
            if (entries.get(i).key.equals(key)) {
                entries.remove(i);
                return true;
            }
        }
        return false;
    }

    public List<Entry<T, V>> getEntries() { return entries; }
}
