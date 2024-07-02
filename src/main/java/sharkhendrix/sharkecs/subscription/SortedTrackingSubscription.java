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

package sharkhendrix.sharkecs.subscription;

import sharkhendrix.sharkecs.util.IntIterator;
import sharkhendrix.sharkecs.util.SortableIntList;

public class SortedTrackingSubscription extends Subscription {

    private SortableIntList entities;
    private IntIterator iterator;

    public SortedTrackingSubscription(SortableIntList entities) {
        this.entities = entities;
        iterator = entities.iterator();
    }

    @Override
    public void add(int entity) {
        entities.add(entity);
        super.add(entity);
    }

    @Override
    public void remove(int entity) {
        entities.remove(entity);
        super.remove(entity);
    }

    @Override
    public IntIterator iterator() {
        entities.sort();
        iterator.reset();
        return iterator;
    }

    public SortableIntList entities() {
        return entities;
    }
}
