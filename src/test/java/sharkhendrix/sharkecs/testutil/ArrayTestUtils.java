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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ArrayTestUtils {

    public static void assertEqualsAnyOrder(Object[] actual, Object... expected) {
        Assertions.assertEquals(expected.length, actual.length);
        List<Object> remainingValues = new ArrayList<>(Arrays.asList(actual));
        for (int i = 0; i < expected.length; i++) {
            if (!remainingValues.remove(expected[i])) {
                Assertions.fail("Array element not found: " + expected[i]);
            }
        }
    }
}
