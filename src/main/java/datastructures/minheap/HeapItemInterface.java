package datastructures.minheap;

public interface HeapItemInterface<T> extends Comparable<T>{
    void setHeapIndex(int index);
    int getHeapIndex();
}
