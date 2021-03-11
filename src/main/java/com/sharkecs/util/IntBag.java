package com.sharkecs.util;

/**
 * <p>
 * Fast but unsafe list of primitive integers, must be used carefully.
 * 
 * <p>
 * Removal operations may break elements ordering and does not clear removed
 * values in the backing array.
 * 
 * @author Joannick Gardize
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
     * to avoid array shift operation.
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

    /**
     * Retrieve and remove the last element.
     * 
     * @return
     */
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
     * @return
     */
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
