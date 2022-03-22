package controller.quicksort;

public class SortObject<V> {
    public final V object;
    public final int sortParameter;
    public SortObject(V object, int sortParameter) {
        this.object = object;
        this.sortParameter = sortParameter;
    }
}