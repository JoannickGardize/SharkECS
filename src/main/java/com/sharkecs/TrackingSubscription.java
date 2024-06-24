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

package com.sharkecs;

import com.sharkecs.annotation.SkipInject;
import com.sharkecs.util.IntBag;

/**
 * A subscription tracking the actual collection of entities. The ordering of
 * entities is arbitrary and may change other time.
 *
 * @author Joannick Gardize
 */
@SkipInject
public class TrackingSubscription extends Subscription {

    private IntBag entities;
    private IntBag entityIndexes;

    public TrackingSubscription(int expectedEntityCount) {
        entities = new IntBag(expectedEntityCount);
        entityIndexes = new IntBag(expectedEntityCount);
    }

    @Override
    public void add(int entityId) {
        entityIndexes.put(entityId, entities.size());
        entities.add(entityId);
        super.add(entityId);
    }

    @Override
    public void remove(int entityId) {
        int removeIndex = entityIndexes.get(entityId);
        entityIndexes.unsafeSet(entities.remove(removeIndex), removeIndex);
        super.remove(entityId);
    }

    @Override
    public IntBag getEntities() {
        return entities;
    }
}
