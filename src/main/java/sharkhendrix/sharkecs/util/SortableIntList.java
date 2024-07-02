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
 * A combination of a collection of int primitives and a sort algorithm,
 * to encourage optimized algorithms, by knowing the internal structure.
 * <p>
 * The collection is intended to be regularly sorted regarding a dynamic sorting function,
 * so usually the int values of this list are reference to other data.
 */
public interface SortableIntList {

    /**
     * Sort this collection, it is usually supposed to be partially sorted,
     * since this method is intended to be called regularly.
     */
    void sort();

    /**
     * Add an element to this list.
     * It is intended to place it in a 'probably' good place for sorting.
     *
     * @param value the new element to add to this list
     */
    void add(int value);

    /**
     * Remove the first element with the given value of this list,
     * keeping the other values to the same relative order.
     *
     * @param value the value to find and remove from this list
     */
    void remove(int value);

    /**
     * @return a new IntIterator view of this list
     */
    IntIterator iterator();
}
