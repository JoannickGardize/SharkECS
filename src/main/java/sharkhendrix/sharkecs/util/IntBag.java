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

/**
 * <p>
 * Fast but unsafe list of primitive integers, must be used carefully.
 *
 * <p>
 * Removal operations may break elements ordering and may not clear removed
 * values in the backing array. Some methods does not update the size.
 */
public class IntBag {

    private int[] data;
    private int size;

    /**
     * @param initialCapacity the initial capacity of the backing array
     */
    public IntBag(int initialCapacity) {
        data = new int[initialCapacity];
        size = 0;
    }

    /**
     * @return the backing data array
     */
    public int[] getData() {
        return data;
    }

    /**
     * Get the value at the given index, does not check if the given index is valid.
     *
     * @param index
     * @return
     */
    public int get(int index) {
        return data[index];
    }

    /**
     * Add the given value at the end of this list
     *
     * @param i
     */
    public void add(int i) {
        ensureCapacity(size);
        data[size] = i;
        size++;
    }

    /**
     * Add another integer bag to the end of this bag.
     *
     * @param other
     */
    public void addAll(IntBag other) {
        int newSize = size + other.size;
        ensureCapacity(newSize - 1);
        System.arraycopy(other.data, 0, data, size, other.size);
        size = newSize;
    }

    /**
     * Remove the element at the given index, Move the last data to the removed data
     * position to avoid array shift operation.
     *
     * @param index
     * @return the data that took the place at the removed index, may return the
     * removed data itself if this was the last index
     */
    public int remove(int index) {
        size--;
        int movedData = data[size];
        data[index] = movedData;
        return movedData;
    }

    /**
     * Retrieve and remove the last element.
     *
     * @return
     */
    public int removeLast() {
        size--;
        return data[size];
    }

    public void removeKeepOrder(int index) {
        System.arraycopy(data, index + 1, data, index, size - index - 1);
        size--;
    }

    /**
     * Does not check the backing array's length and does not update size.
     *
     * @param index
     * @param i
     */
    public void unsafeSet(int index, int i) {
        data[index] = i;
    }

    /**
     * Grow the array if necessary, does not update size.
     *
     * @param index
     * @param i
     */
    public void put(int index, int i) {
        ensureCapacity(index);
        data[index] = i;
    }

    /**
     * Grow the array if necessary, update the size.
     *
     * @param index
     * @param i
     */
    public void set(int index, int i) {
        put(index, i);
        if (index >= size) {
            size = index + 1;
        }
    }

    public void insert(int index, int value) {
        ensureCapacity(size);
        System.arraycopy(data, index, data, index + 1, size - index);
        data[index] = value;
        size++;
    }

    /**
     * Set the size to zero, does not clear the backing array
     */
    public void clear() {
        size = 0;
    }

    /**
     * @return the size of this bag, if maintained
     */
    public int size() {
        return size;
    }

    /**
     * Convenience method for {@code size() == 0}
     *
     * @return true if this bag is empty, false if it has at least one element
     */
    public boolean isEmpty() {
        return size == 0;
    }

    public int[] toArray() {
        int[] result = new int[size];
        System.arraycopy(data, 0, result, 0, size);
        return result;
    }

    public IntIterator iterator() {
        return new IntIterator() {

            private int currentIndex = 0;

            @Override
            public boolean hasNext() {
                return currentIndex < size;
            }

            @Override
            public int next() {
                return data[currentIndex++];
            }

            @Override
            public void reset() {
                currentIndex = 0;
            }

            @Override
            public int totalSize() {
                return size;
            }
        };
    }


    private void ensureCapacity(int requiredIndex) {
        if (requiredIndex >= data.length) {
            int[] newArray = new int[(requiredIndex + 1) * 2];
            System.arraycopy(data, 0, newArray, 0, data.length);
            data = newArray;
        }
    }
}
