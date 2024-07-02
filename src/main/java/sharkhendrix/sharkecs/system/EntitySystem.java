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

package sharkhendrix.sharkecs.system;

import sharkhendrix.sharkecs.EntityManager;
import sharkhendrix.sharkecs.Initializable;
import sharkhendrix.sharkecs.Processor;
import sharkhendrix.sharkecs.annotation.ForceInject;
import sharkhendrix.sharkecs.subscription.SubscriberAdapter;

/**
 * Base class for an entity processing system.
 */
public abstract class EntitySystem extends SubscriberAdapter implements Processor, Initializable {

    @ForceInject
    protected EntityManager entityManager;

    @Override
    public void initialize() {
        // Nothing by default
    }

    public void setEntityManager(EntityManager entityManager) {
        this.entityManager = entityManager;
    }
}
