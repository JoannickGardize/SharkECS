package com.sharkecs.util;

import java.util.function.Supplier;

public class Bag<T> {

    protected Object[] data;
    protected int size;

    public Bag(int initialCapacity) {
        data = new Object[initialCapacity];
        size = 0;
    }

    public Object[] getData() {
        return data;
    }

    @SuppressWarnings("unchecked")
    public T get(int index) {
        return (T) data[index];
    }

    public void add(T o) {
        ensureCapacity(size);
        data[size] = o;
        size++;
    }

    public T nextOrAdd(Supplier<T> newInstanceSupplier) {
        ensureCapacity(size);
        @SuppressWarnings("unchecked")
        T result = (T) data[size];
        if (result == null) {
            result = newInstanceSupplier.get();
            data[size] = result;
        }
        size++;
        return result;
    }

    public void addAll(Bag<T> other) {
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
    @SuppressWarnings("unchecked")
    public T remove(int index) {
        size--;
        T movedData = (T) data[size];
        data[index] = movedData;
        return movedData;
    }

    @SuppressWarnings("unchecked")
    public T removeLast() {
        size--;
        return (T) data[size];
    }

    /**
     * Grow the array if necessary, does not update size.
     * 
     * @param index
     * @param i
     */
    public void unsafeSet(int index, T o) {
        ensureCapacity(index);
        data[index] = o;
    }

    /**
     * Grow the array if necessary, update the size.
     * 
     * @param index
     * @param i
     */
    public void set(int index, T o) {
        unsafeSet(index, o);
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

    public int size() {
        return size;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    protected void ensureCapacity(int requiredIndex) {
        if (requiredIndex >= data.length) {
            int oldLength = data.length;
            Object[] newArray = new Object[(requiredIndex + 1) * 2];
            System.arraycopy(data, 0, newArray, 0, data.length);
            data = newArray;
            grownUp(oldLength);
        }
    }

    protected void grownUp(int oldLength) {
        // For override
    }
}
