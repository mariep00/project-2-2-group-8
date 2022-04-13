package gamelogic.agent;

public interface HeapItemInterface<T> extends Comparable<T>{
    public void setHeapIndex(int index);
    public int getHeapIndex();
}
