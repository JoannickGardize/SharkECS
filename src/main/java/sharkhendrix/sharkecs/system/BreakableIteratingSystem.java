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

import sharkhendrix.sharkecs.util.IntIterator;

/**
 * Similar to {@link IteratingSystem} but able to stop a process run after any entity process.
 * <p>
 * Usually, this system will process sorted entities via {@link sharkhendrix.sharkecs.annotation.SortEntities}.
 * <p>
 * Also, able to discard all the process via {@link #beginProcess()}.
 */
public abstract class BreakableIteratingSystem extends EntitySystem {

    /**
     * Called at the beginning of a process run. Returns false by default.
     *
     * @return true to discard the run of this system during this process call
     * ({@link #endProcess()} will not be called too)
     */
    protected boolean beginProcess() {
        return false;
    }

    /**
     * Process the given entity. Called for each subscribed entities at each process
     * run, in an arbitrary order, or in the given order if a {@link sharkhendrix.sharkecs.annotation.SortEntities}
     * annotation is present. if a call returns false, the actual process run stop
     * and the remaining entities are discarded.
     *
     * @param entity the entity to process
     * @return true to stop the current process run of this system, false to continue
     */
    protected abstract boolean process(int entity);

    /**
     * Called at the end of a process run. Does nothing by default.
     */
    protected void endProcess() {
        // Nothing by default
    }

    @Override
    public void process() {
        if (beginProcess()) {
            return;
        }
        IntIterator it = entityIterator();
        while (it.hasNext()) {
            if (process(it.next())) {
                break;
            }
        }
        endProcess();
    }
}
