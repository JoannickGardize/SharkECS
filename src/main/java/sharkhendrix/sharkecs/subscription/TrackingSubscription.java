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

import sharkhendrix.sharkecs.annotation.SkipInject;
import sharkhendrix.sharkecs.util.IntBag;
import sharkhendrix.sharkecs.util.IntIterator;

/**
 * A subscription tracking the actual collection of entities. The ordering of
 * entities is arbitrary and may change other time.
 */
@SkipInject
public class TrackingSubscription extends Subscription {

    private IntBag entities;
    private IntBag entityIndexes;
    private IntIterator iterator;

    public TrackingSubscription(int expectedEntityCount) {
        entities = new IntBag(expectedEntityCount);
        entityIndexes = new IntBag(expectedEntityCount);
        iterator = entities.iterator();
    }

    @Override
    public void add(int entity) {
        entityIndexes.put(entity, entities.size());
        entities.add(entity);
        super.add(entity);
    }

    @Override
    public void remove(int entity) {
        int removeIndex = entityIndexes.get(entity);
        entityIndexes.unsafeSet(entities.remove(removeIndex), removeIndex);
        super.remove(entity);
    }

    @Override
    public IntIterator iterator() {
        iterator.reset();
        return iterator;
    }
}
