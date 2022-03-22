package controller.quicksort;

public class QuickSort<V> {
    private int partition (SortObject<V> a[], int start, int end) {
        SortObject<V> pivot = a[end];
        int i = (start - 1);

        for (int j = start; j <= end - 1; j++) {
            if (a[j].sortParameter < pivot.sortParameter) {
                i++;
                SortObject<V> t = a[i];
                a[i] = a[j];
                a[j] = t;
            }
        }
        int t = a[i+1].sortParameter;
        a[i+1] = a[end];
        a[end] = getSortObjct(a, t);
        return (i + 1);
    }

    private SortObject<V> getSortObjct(SortObject<V> a[], int sortParameter) {
        for (SortObject<V> vSortObject : a) {
            if (vSortObject.sortParameter == sortParameter) return vSortObject;
        }
        return null;
    }

    SortObject<V>[] sort(SortObject<V> a[], int start, int end) {
        if (start < end) {
            int p = partition(a, start, end);  //p is partitioning index
            sort(a, start, p - 1);
            sort(a, p + 1, end);
        }
        return a;
    }
}
