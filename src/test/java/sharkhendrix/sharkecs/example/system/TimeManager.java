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

import sharkhendrix.sharkecs.Processor;
import sharkhendrix.sharkecs.example.misc.Time;
import sharkhendrix.sharkecs.example.system.annotation.LogicPhase;

@LogicPhase
public class TimeManager implements Processor {

    private Time time;

    @Override
    public void process() {
        time.setElapsedTime(time.getElapsedTime() + time.getDeltaTime());
    }

    public void setTime(Time time) {
        this.time = time;
    }
}
