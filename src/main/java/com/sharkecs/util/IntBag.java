package com.sharkecs.util;

public class IntBag {

    private int[] data;
    private int size;

    public IntBag(int initialCapacity) {
        data = new int[initialCapacity];
        size = 0;
    }

    public int[] getData() {
        return data;
    }

    public int get(int index) {
        return data[index];
    }

    public void add(int i) {
        ensureCapacity(size);
        data[size] = i;
        size++;
    }

    public void addAll(IntBag other) {
        int newSize = size + other.size;
        ensureCapacity(newSize - 1);
        System.arraycopy(other.data, 0, data, size, other.size);
        size = newSize;
    }

    /**
     * Does not keep elements ordering.
     * 
     * @param index
     * @return the data that took the place at the removed index, may return the
     *         removed data itself if this was the last index
     */
    public int remove(int index) {
        size--;
        int movedData = data[size];
        data[index] = movedData;
        return movedData;
    }

    public int removeLast() {
        size--;
        return data[size];
    }

    /**
     * Grow the array if necessary, does not update size.
     * 
     * @param index
     * @param i
     */
    public void unsafeSet(int index, int i) {
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
        unsafeSet(index, i);
        if (index >= size) {
            size = index + 1;
        }
    }

    public void clear() {
        size = 0;
    }

    public int size() {
        return size;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    private void ensureCapacity(int requiredIndex) {
        if (requiredIndex >= data.length) {
            int[] newArray = new int[(requiredIndex + 1) * 2];
            System.arraycopy(data, 0, newArray, 0, data.length);
            data = newArray;
        }
    }
}
