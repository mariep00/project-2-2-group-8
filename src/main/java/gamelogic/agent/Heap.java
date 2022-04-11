package gamelogic.agent;

@SuppressWarnings("unchecked")

public class Heap<T extends Comparable<T>> {

    T[] items;
    int itemCount;
    
    public Heap(int size) {
        items = (T[])new Object[size];
        itemCount = 0;
    }

    public void add(T item) {
        items[itemCount] = item;
        sortUp();
        itemCount++;  
    }

    public T removeFirst() {
        T firstItem = items[0];
        itemCount--;
        swap(0, itemCount);
        items[itemCount] = null;
        sortDown();

        return firstItem;
    }

    private void sortUp() {
        int index = itemCount;

        while(hasParent(index) && (parent(index).compareTo(items[index]) > 0)) {
            int parentIndex = parentIndex(index);
            swap(index, parentIndex);
            index = parentIndex;
        }
    }

    private T parent(int index) {
        return items[parentIndex(index)];
    }

    private int parentIndex(int index) {
        return (index-1)/2;
    }

    private boolean hasParent(int index) {
        return index>0;
    }

    private void swap(int index, int parentIndex) {
        T tmp = items[index];
        items[index] = items[parentIndex];
        items[parentIndex] = tmp;
    }

    private int leftChildIndex(int index) {
        return (2*index)+1;
    }

    private int rightChildIndex(int index) {
        return (2*index)+2;
    }

    private void sortDown() {
        int index = 0;
        while(hasLeftChild(index)) {
            int smaller = leftChildIndex(index);
            if (hasRightChild(index) && items[smaller].compareTo(items[rightChildIndex(index)]) > 0) {
                smaller = rightChildIndex(index);
            }
            if (items[index].compareTo(items[smaller]) > 0) {
                swap(index, smaller);
            } else {break;}
            index = smaller;
        }
    }

    private boolean hasRightChild(int index) {
        return rightChildIndex(index)<itemCount;
    }

    private boolean hasLeftChild(int index) {
        return leftChildIndex(index)<itemCount;
    }

    public void updateItem(T item) {
        
    }
}
