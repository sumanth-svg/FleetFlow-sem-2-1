package com.fleetflow.analytics;

import com.fleetflow.model.PackageItem;

import java.util.Comparator;
import java.util.List;
import java.util.Random;

/**
 * Randomized QuickSort algorithm.
 * Selects pivots uniformly at random and uses 3-way Dutch National Flag partitioning for duplicate keys.
 * Guarantees expected O(N log N) time and eliminates O(N^2) degradation on sorted or adversarial inputs.
 * Used in FleetFlow for high-performance ranking of delivery packages by priority, weight, and value.
 */
public class RandomizedQuickSort {

    private static final Random random = new Random();

    public static <T> void sort(List<T> list, Comparator<T> comparator) {
        if (list == null || list.size() <= 1) return;
        quickSort(list, 0, list.size() - 1, comparator);
    }

    private static <T> void quickSort(List<T> list, int low, int high, Comparator<T> comp) {
        if (low >= high) return;

        // Choose random pivot and swap with low
        int pivotIndex = low + random.nextInt(high - low + 1);
        swap(list, low, pivotIndex);
        T pivot = list.get(low);

        // 3-way partition: elements < pivot, == pivot, > pivot
        int lt = low;
        int gt = high;
        int i = low + 1;

        while (i <= gt) {
            int cmp = comp.compare(list.get(i), pivot);
            if (cmp < 0) {
                swap(list, lt++, i++);
            } else if (cmp > 0) {
                swap(list, i, gt--);
            } else {
                i++;
            }
        }

        // Elements from lt to gt are equal to pivot
        quickSort(list, low, lt - 1, comp);
        quickSort(list, gt + 1, high, comp);
    }

    private static <T> void swap(List<T> list, int i, int j) {
        T temp = list.get(i);
        list.set(i, list.get(j));
        list.set(j, temp);
    }

    /**
     * Standard logistics comparator: sorts by priority rank (URGENT > HIGH > STANDARD > LOW)
     * descending, then by package weight descending.
     */
    public static final Comparator<PackageItem> LOGISTICS_PRIORITY_COMPARATOR = (p1, p2) -> {
        int pr1 = p1.getPriorityRank();
        int pr2 = p2.getPriorityRank();
        if (pr1 != pr2) {
            return Integer.compare(pr2, pr1); // higher rank first
        }
        return Double.compare(p2.getWeightKg(), p1.getWeightKg()); // heavier package first
    };
}
