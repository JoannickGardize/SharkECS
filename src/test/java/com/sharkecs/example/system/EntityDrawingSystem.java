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

import com.sharkecs.ComponentMapper;
import com.sharkecs.IteratingSystem;
import com.sharkecs.annotation.WithAll;
import com.sharkecs.example.component.Bullet;
import com.sharkecs.example.component.Corpse;
import com.sharkecs.example.component.Physics;
import com.sharkecs.example.component.Shooter;
import com.sharkecs.example.component.Shooter.ShooterCommand;
import com.sharkecs.example.singleton.Viewport;
import com.sharkecs.example.system.annotation.DrawingPhase;

import java.util.ArrayList;
import java.util.List;

@DrawingPhase
@WithAll(Physics.class)
public class EntityDrawingSystem extends IteratingSystem {

    private static final String[] PLAYER = {" o ", "/|\\", "/ \\"};
    private static final String[] PLAYER_LEFT = {" o ", "-|\\", "/ \\"};
    private static final String[] PLAYER_RIGHT = {" o ", "/|-", "/ \\"};
    private static final String[] BULLET_LEFT = {null, "o~-", null};
    private static final String[] BULLET_RIGHT = {null, "-~o", null};
    private static final String[] CORPSE = {null, null, "__,"};

    private static class DrawEntry {
        int position;
        String[] picture;

        DrawEntry(int position, String[] picture) {
            this.position = position;
            this.picture = picture;
        }
    }

    private ComponentMapper<Physics> physicsMapper;
    private ComponentMapper<Shooter> shooterMapper;
    private ComponentMapper<Bullet> bulletMapper;
    private ComponentMapper<Corpse> corpseMapper;

    private Viewport viewport;

    private List<DrawEntry> batch = new ArrayList<>();

    @Override
    protected void beginProcess() {
        batch.clear();
    }

    @Override
    protected void process(int entityId) {
        Physics physics = physicsMapper.get(entityId);
        if (bulletMapper.has(entityId)) {
            batch.add(new DrawEntry(physics.getPosition(), physics.getSpeed() > 0 ? BULLET_RIGHT : BULLET_LEFT));
            return;
        }
        Shooter shooter = shooterMapper.get(entityId);
        if (shooter != null) {
            batch.add(new DrawEntry(physics.getPosition(), pictureForShooter(shooter)));
        } else if (corpseMapper.has(entityId)) {
            batch.add(new DrawEntry(physics.getPosition(), CORPSE));
        }
    }

    @Override
    protected void endProcess() {
        batch.sort((e1, e2) -> Integer.compare(e1.position, e2.position));
        drawLine(0);
        drawLine(1);
        drawLine(2);
    }

    private void drawLine(int lineIndex) {
        int lastDrawingPosition = viewport.getStart() - 1;
        for (DrawEntry entry : batch) {
            if (entry.position < viewport.getStart() || entry.position == lastDrawingPosition) {
                continue;
            } else if (entry.position >= viewport.getEnd()) {
                break;
            }
            if (entry.picture[lineIndex] != null) {
                for (int i = lastDrawingPosition + 1; i < entry.position; i++) {
                    System.out.print("   ");
                }
                System.out.print(entry.picture[lineIndex]);
                lastDrawingPosition = entry.position;
            }
        }
        System.out.println();
    }

    private String[] pictureForShooter(Shooter shooter) {
        if (shooter.getCommand() == ShooterCommand.LEFT) {
            return PLAYER_LEFT;
        } else if (shooter.getCommand() == ShooterCommand.RIGHT) {
            return PLAYER_RIGHT;
        } else {
            return PLAYER;
        }
    }

    public void setPhysicsMapper(ComponentMapper<Physics> physicsMapper) {
        this.physicsMapper = physicsMapper;
    }

    public void setShooterMapper(ComponentMapper<Shooter> shooterMapper) {
        this.shooterMapper = shooterMapper;
    }

    public void setBulletMapper(ComponentMapper<Bullet> bulletMapper) {
        this.bulletMapper = bulletMapper;
    }

    public void setCorpseMapper(ComponentMapper<Corpse> corpseMapper) {
        this.corpseMapper = corpseMapper;
    }

    public void setViewport(Viewport viewport) {
        this.viewport = viewport;
    }
}
