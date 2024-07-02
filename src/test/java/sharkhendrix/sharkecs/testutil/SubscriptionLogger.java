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

package sharkhendrix.sharkecs.testutil;

import org.junit.jupiter.api.Assertions;
import sharkhendrix.sharkecs.Transmutation;
import sharkhendrix.sharkecs.subscription.SubscriptionListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class SubscriptionLogger implements SubscriptionListener {

    private List<Integer> addLog = new ArrayList<>();
    private List<Integer> removeLog = new ArrayList<>();
    private List<Integer> changeLog = new ArrayList<>();
    private List<Transmutation> transmutationLog = new ArrayList<>();

    @Override
    public void removed(int entity) {
        removeLog.add(entity);
    }

    @Override
    public void added(int entity) {
        addLog.add(entity);
    }

    @Override
    public void changed(int entity, Transmutation transmutation) {
        changeLog.add(entity);
        transmutationLog.add(transmutation);
    }

    public void assertAddLog(int... ids) {
        assertLog(addLog, ids);
    }

    public void assertRemoveLog(int... ids) {
        assertLog(removeLog, ids);
    }

    public void assertChangeLog(int... ids) {
        assertLog(changeLog, ids);
    }

    public void assertTransmutationLog(Transmutation... transmutations) {
        Assertions.assertArrayEquals(transmutations, transmutationLog.toArray());
    }

    private void assertLog(List<Integer> log, int... ids) {
        Assertions.assertEquals(Arrays.stream(ids).mapToObj(Integer::valueOf).collect(Collectors.toList()), log);
    }

    public void clear() {
        addLog.clear();
        removeLog.clear();
        changeLog.clear();
        transmutationLog.clear();
    }
}
