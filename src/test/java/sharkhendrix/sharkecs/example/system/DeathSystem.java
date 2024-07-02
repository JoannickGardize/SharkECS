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

package sharkhendrix.sharkecs.example.system;

import sharkhendrix.sharkecs.Archetype;
import sharkhendrix.sharkecs.ComponentMapper;
import sharkhendrix.sharkecs.IteratingSystem;
import sharkhendrix.sharkecs.annotation.With;
import sharkhendrix.sharkecs.example.component.Health;
import sharkhendrix.sharkecs.example.component.Image;
import sharkhendrix.sharkecs.example.component.Physics;
import sharkhendrix.sharkecs.example.misc.Images;
import sharkhendrix.sharkecs.example.system.annotation.LogicPhase;

@LogicPhase
@With({Physics.class, Health.class})
public class DeathSystem extends IteratingSystem {

    private ComponentMapper<Health> healthMapper;
    private ComponentMapper<Image> imageMapper;

    private Archetype corpse;

    @Override
    protected void process(int entity) {
        if (healthMapper.get(entity).getValue() <= 0) {
            entityManager.transmute(entity, corpse);
            imageMapper.create(entity).setImage(Images.CORPSE);
        }
    }

    public void setHealthMapper(ComponentMapper<Health> healthMapper) {
        this.healthMapper = healthMapper;
    }

    public void setImageMapper(ComponentMapper<Image> imageMapper) {
        this.imageMapper = imageMapper;
    }

    public void setCorpse(Archetype corpse) {
        this.corpse = corpse;
    }
}
