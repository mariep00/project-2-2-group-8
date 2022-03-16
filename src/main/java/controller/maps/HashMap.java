package controller.maps;

import controller.Vector2D;
import controller.maps.graph.Node;

import java.util.LinkedList;
import java.util.List;

public class HashMap {
    private int capacity;
    private Bucket[] buckets;

    final private double loadFactor;
    private int size = 0;
    private int numberOfNodes = 0;

    public HashMap(int capacity) {
        this.capacity = capacity;
        buckets = new Bucket[capacity];
        loadFactor = 0.75;
    }

    public void addEntry(Vector2D key, Node value) {
        numberOfNodes++;
        int hash = getHash(key);
        if (buckets[hash] == null) {
            size++;
            buckets[hash] = new Bucket();
        }
        buckets[hash].addEntry(new Entry(key, value));
        if (shouldExpand()) expand();
    }

    public void removeEntry(Vector2D key) {
        int hash = getHash(key);
        if (buckets[hash] != null) {
            numberOfNodes--;
            buckets[hash].removeEntry(key);
            if (buckets[hash].getEntries().isEmpty()) {
                buckets[hash] = null;
                size--;
            }
        }
    }

    public Node getValue(Vector2D key) {
        Bucket bucket = buckets[getHash(key)];
        if (bucket != null) {
            Entry entry = bucket.getEntry(key);
            if (entry != null) return entry.getValue();
        }
        return null;
    }

    private boolean shouldExpand() { return ((double) size / capacity >= loadFactor); }

    private void expand() {
        capacity = capacity*2;
        Bucket[] oldBuckets = this.buckets;
        buckets = new Bucket[capacity];

        for (Bucket oldBucket : oldBuckets) {
            if (oldBucket != null) {
                for (Entry entry : oldBucket.getEntries()) {
                    int hash = getHash(entry.key);
                    if (buckets[hash] == null) buckets[hash] = new Bucket();
                    buckets[hash].addEntry(entry);
                }
            }
        }
    }

    private int getHash(Vector2D key) {
        int hash = 31+key.x;
        hash = (hash*31)+key.y;
        return hash% capacity;
    }

    public int getNumberOfNodes() { return numberOfNodes; }
}

class Entry {
    public Vector2D key;
    public Node value;

    public Entry(Vector2D key, Node value) {
        this.key = key;
        this.value = value;
    }

    public Node getValue() { return value; }
}

class Bucket {
    private LinkedList<Entry> entries;

    public void addEntry(Entry entry){
        if (entries == null) entries = new LinkedList<>();
        entries.add(entry);
    }
    public Entry getEntry(Vector2D key) {
        for (Entry entry : entries) {
            if (entry.key.equals(key)) return entry;
        }
        return null;
    }
    public void removeEntry(Vector2D key) {
        for (int i = 0; i < entries.size(); i++) {
            if (entries.get(i).key.equals(key)) entries.remove(i);
        }
    }

    public List<Entry> getEntries() { return entries; }
}
