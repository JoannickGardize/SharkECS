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

package com.sharkecs.example.system;

import com.sharkecs.Archetype;
import com.sharkecs.ComponentMapper;
import com.sharkecs.IteratingSystem;
import com.sharkecs.annotation.WithAll;
import com.sharkecs.example.component.Health;
import com.sharkecs.example.component.Physics;
import com.sharkecs.example.system.annotation.LogicPhase;

@LogicPhase
@WithAll({Physics.class, Health.class})
public class DeathSystem extends IteratingSystem {

    private ComponentMapper<Health> healthMapper;

    private Archetype corpse;

    @Override
    protected void process(int entityId) {
        if (healthMapper.get(entityId).getValue() <= 0) {
            entityManager.transmute(entityId, corpse);
        }
    }

    public void setHealthMapper(ComponentMapper<Health> healthMapper) {
        this.healthMapper = healthMapper;
    }

    public void setCorpse(Archetype corpse) {
        this.corpse = corpse;
    }
}
