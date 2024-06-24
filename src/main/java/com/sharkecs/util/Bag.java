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

package com.sharkecs.util;

import java.util.function.Supplier;

/**
 * <p>
 * Fast but unsafe list of objects, must be used carefully.
 *
 * <p>
 * Removal operations may break elements ordering and does not clear removed
 * values in the backing array.
 *
 * @param <T> the elements type
 * @author Joannick Gardize
 */
public class Bag<T> {

    protected Object[] data;
    protected int size;

    /**
     * @param initialCapacity the initial capacity of the backing array
     */
    public Bag(int initialCapacity) {
        data = new Object[initialCapacity];
        size = 0;
    }

    /**
     * @return the backing data array
     */
    public Object[] getData() {
        return data;
    }

    /**
     * Get the value at the given index, does not check if the given index is valid.
     *
     * @param index
     * @return
     */
    @SuppressWarnings("unchecked")
    public T get(int index) {
        return (T) data[index];
    }

    /**
     * Get the value at the given index, or null if the index exceed the backing
     * array's length.
     *
     * @param index
     * @return
     */
    public T getOrNull(int index) {
        if (index >= data.length) {
            return null;
        } else {
            return get(index);
        }
    }

    /**
     * Add the given value at the end of this list
     *
     * @param o
     */
    public void add(T o) {
        ensureCapacity(size);
        data[size] = o;
        size++;
    }

    /**
     * Add a value at the end of the list, if a value was present at the new last
     * index, it is reused.
     *
     * @param newInstanceSupplier the supplier used to create a new instance if
     *                            there is not
     * @return the new value, may be recycled
     */
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

    /**
     * Add another integer bag to the end of this bag.
     *
     * @param other
     */
    public void addAll(Bag<T> other) {
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
     * removed data itself if this was the last index
     */
    @SuppressWarnings("unchecked")
    public T remove(int index) {
        size--;
        T movedData = (T) data[size];
        data[index] = movedData;
        return movedData;
    }

    /**
     * Retrieve and remove the last element.
     *
     * @return
     */
    @SuppressWarnings("unchecked")
    public T removeLast() {
        size--;
        return (T) data[size];
    }

    /**
     * Does not check the backing array's length and does not update size.
     *
     * @param index
     * @param o
     */
    public void unsafeSet(int index, T o) {
        data[index] = o;
    }

    /**
     * Grow the array if necessary, does not update size.
     *
     * @param index
     * @param o
     */
    public void put(int index, T o) {
        ensureCapacity(index);
        data[index] = o;
    }

    /**
     * Grow the array if necessary, update the size.
     *
     * @param index
     * @param o
     */
    public void set(int index, T o) {
        put(index, o);
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

    protected void ensureCapacity(int requiredIndex) {
        if (requiredIndex >= data.length) {
            Object[] newArray = new Object[(requiredIndex + 1) * 2];
            System.arraycopy(data, 0, newArray, 0, data.length);
            data = newArray;
        }
    }
}
