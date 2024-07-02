/*
 * Copyright 2024 Joannick Gardize
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package sharkhendrix.sharkecs.util;

import java.util.Arrays;

/**
 * A sortable int list backed by an array.
 * <p>
 * The {@link #sort()} algorithm is the insertion algorithm,
 * which is as simple as efficient for smoothly changing comparison values (e.g. z-order of moving entities).
 * <p>
 * The {@link #add(int)} method uses a binary search to insert the value.
 * It may insert in the wrong place since the array may be partially sorted,
 * but it's still beneficial for the insertion sort algorithm.
 * <p>
 * In the same way, the {@link #remove(int)} method uses a binary search to find and remove the value.
 * In the worst case, because the array may be partially sorted, the binary search fail, in that case,
 * the value is 'brute-force' searched starting from the binary search result.
 */
public class SortableIntArrayList implements SortableIntList {
    private IntBag bag;
    private IntComparator comparator;

    public SortableIntArrayList(int initialCapacity, IntComparator comparator) {
        this.comparator = comparator;
        bag = new IntBag(initialCapacity);
    }

    @Override
    public void sort() {
        int[] array = bag.getData();
        int length = bag.size();
        for (int i = 1; i < length; i++) {
            int j = i - 1;
            int value = array[i];
            if (comparator.compare(value, array[j]) < 0) {
                while (j > 0 && comparator.compare(value, array[j - 1]) < 0) {
                    j--;
                }
                if (i - j == 1) {
                    int tmp = array[i];
                    array[i] = array[j];
                    array[j] = tmp;
                } else {
                    System.arraycopy(array, j, array, j + 1, (i - j));
                }
                array[j] = value;
            }
        }
    }

    @Override
    public void add(int value) {
        int index = binarySearch(value, true);
        bag.insert(index, value);
    }

    @Override
    public void remove(int value) {
        int index = binarySearch(value, false);
        if (bag.get(index) != value) {
            int size = bag.size();
            int[] data = bag.getData();
            int max = Math.max(size - index, index);
            for (int i = 0; i < max; i++) {
                int aroundIndex = index + i;
                if (aroundIndex < size && data[aroundIndex] == value) {
                    index = aroundIndex;
                    break;
                }
                aroundIndex = index - i;
                if (aroundIndex >= 0 && data[aroundIndex] == value) {
                    index = aroundIndex;
                    break;
                }
            }
        }
        bag.removeKeepOrder(index);
    }

    @Override
    public IntIterator iterator() {
        return bag.iterator();
    }

    public IntComparator getComparator() {
        return comparator;
    }

    @Override
    public String toString() {
        return Arrays.toString(bag.getData());
    }

    private int binarySearch(int value, boolean insertionIndex) {
        int[] array = bag.getData();
        int low = 0;
        int high = bag.size();
        int compare = 0;
        int mid = 0;
        while (low < high) {
            mid = (low + high) / 2;
            compare = comparator.compare(value, array[mid]);
            if (compare < 0) {
                high = mid;
            } else if (compare > 0) {
                low = mid + 1;
            } else {
                break;
            }
        }
        if (insertionIndex && compare > 0) {
            mid++;
        }
        return mid;
    }
}
