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

import sharkhendrix.sharkecs.util.IntBag;
import org.junit.jupiter.api.Assertions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class BagTestUtils {

    public static void assertBagEquals(IntBag bag, int... values) {
        assertBagEquals(true, bag, values);
    }

    public static void assertBagEquals(boolean assertSize, IntBag bag, int... values) {
        if (assertSize) {
            Assertions.assertEquals(values.length, bag.size());
        }
        for (int i = 0; i < values.length; i++) {
            Assertions.assertEquals(values[i], bag.get(i));
        }
    }

    public static void assertBagEqualsAnyOrder(IntBag bag, int... values) {
        Assertions.assertEquals(values.length, bag.size());
        List<Integer> remainingValues = new ArrayList<>(Arrays.stream(values).mapToObj(i -> Integer.valueOf(i)).collect(Collectors.toList()));
        for (int i = 0; i < bag.size(); i++) {
            if (!remainingValues.remove(Integer.valueOf(bag.get(i)))) {
                Assertions.fail("Bag element not found");
            }
        }
    }
}
