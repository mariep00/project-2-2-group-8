package gamelogic.agent;

@SuppressWarnings("unchecked")

public class Heap<T extends HeapItemInterface<T>> {

    T[] items;
    int itemCount;
    
    public Heap(int size) {
        items = (T[])new HeapItemInterface[size];
        itemCount = 0;
    }

    public void add(T item) {
        items[itemCount] = item;
        item.setHeapIndex(itemCount);
        sortUp(itemCount);
        itemCount++;  
    }

    public T removeFirst() {
        T firstItem = items[0];
        itemCount--;
        swap(0, itemCount);
        items[itemCount] = null;
        sortDown(0);

        return firstItem;
    }

    private void sortUp(int index) {
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
        items[index].setHeapIndex(index);
        items[parentIndex].setHeapIndex(parentIndex);
    }

    private int leftChildIndex(int index) {
        return (2*index)+1;
    }

    private int rightChildIndex(int index) {
        return (2*index)+2;
    }

    private void sortDown(int index) {
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
        sortUp(item.getHeapIndex());
        sortDown(item.getHeapIndex());
    }

    public T contains(T item) {
        for (int i=0; i < itemCount; i++) {
            if (items[i].equals(item)) return items[i];
        }
        return null;
    }
}
