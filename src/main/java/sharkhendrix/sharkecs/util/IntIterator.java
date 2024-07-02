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
 * Iterator of primitive int collections.
 */
public interface IntIterator {

    boolean hasNext();

    int next();

    /**
     * Resets this iterator so that it starts again to the beginning of the collection.
     */
    void reset();

    /**
     * @return the size of the collection in the back of this iterator.
     */
    int totalSize();

    default int[] toArray() {
        IntBag ints = new IntBag(totalSize());
        while (hasNext()) {
            ints.add(next());
        }
        return ints.toArray();
    }
}
