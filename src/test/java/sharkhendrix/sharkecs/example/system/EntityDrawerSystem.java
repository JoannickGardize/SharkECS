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

import sharkhendrix.sharkecs.ComponentMapper;
import sharkhendrix.sharkecs.annotation.SortEntities;
import sharkhendrix.sharkecs.annotation.With;
import sharkhendrix.sharkecs.example.component.Image;
import sharkhendrix.sharkecs.example.component.Physics;
import sharkhendrix.sharkecs.example.misc.Viewport;
import sharkhendrix.sharkecs.example.system.annotation.DrawingPhase;
import sharkhendrix.sharkecs.system.EntitySystem;
import sharkhendrix.sharkecs.util.IntIterator;

@DrawingPhase
@SortEntities("position")
@With({Physics.class, Image.class})
public class EntityDrawerSystem extends EntitySystem {

    private ComponentMapper<Physics> physicsMapper;
    private ComponentMapper<Image> imageMapper;


    private Viewport viewport;

    @Override
    public void process() {
        drawLine(0);
        drawLine(1);
        drawLine(2);
    }

    private void drawLine(int lineIndex) {
        int lastDrawingPosition = viewport.getStart() - 1;
        IntIterator it = entityIterator();
        while (it.hasNext()) {
            int entity = it.next();
            int position = physicsMapper.get(entity).getPosition();
            if (position < viewport.getStart()
                    || position == lastDrawingPosition
                    || position >= viewport.getEnd()) {
                continue;
            }
            String[] image = imageMapper.get(entity).getImage();
            if (image[lineIndex] != null) {
                for (int i = lastDrawingPosition + 1; i < position; i++) {
                    System.out.print("   ");
                }
                System.out.print(image[lineIndex]);
                lastDrawingPosition = position;
            }
        }
        System.out.println();
    }

    public void setPhysicsMapper(ComponentMapper<Physics> physicsMapper) {
        this.physicsMapper = physicsMapper;
    }

    public void setImageMapper(ComponentMapper<Image> imageMapper) {
        this.imageMapper = imageMapper;
    }

    public void setViewport(Viewport viewport) {
        this.viewport = viewport;
    }
}
