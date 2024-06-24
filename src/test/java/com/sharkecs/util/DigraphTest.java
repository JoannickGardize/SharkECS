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

package com.sharkecs.util;

import com.sharkecs.testutil.ArrayTestUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

class DigraphTest {

    @Test
    void topologicalSortTest() throws GraphCycleException {
        String o1 = "o1";
        String o2 = "o2";
        String o3 = "o3";
        String o4 = "o4";
        String o5 = "o5";
        String o6 = "o6";
        String o7 = "o7";
        String o8 = "o8";
        String o9 = "o9";
        Digraph<Object> digraph = new Digraph<>();

        digraph.addEdge(o1, o2);
        digraph.addEdge(o1, o4);

        digraph.addEdge(o2, o4);
        digraph.addEdge(o2, o8);
        digraph.addEdge(o2, o7);

        digraph.addEdge(o3, o4);

        digraph.addEdge(o4, o5);
        digraph.addEdge(o4, o6);
        digraph.addEdge(o4, o7);

        digraph.addEdge(o5, o7);
        digraph.addEdge(o8, o7);
        digraph.addEdge(o9, o6);

        List<Object> depths = digraph.topologicalSort();

        Assertions.assertEquals(9, depths.size());
        ArrayTestUtils.assertEqualsAnyOrder(depths.subList(0, 3).toArray(), o1, o9, o3);
        Assertions.assertEquals(o2, depths.get(3));
        ArrayTestUtils.assertEqualsAnyOrder(depths.subList(4, 6).toArray(), o8, o4);
        ArrayTestUtils.assertEqualsAnyOrder(depths.subList(6, 8).toArray(), o5, o6);
        Assertions.assertEquals(o7, depths.get(8));

        Digraph<Object> digraph2 = new Digraph<>();

        digraph2.addEdge(o1, o2);
        digraph2.addEdge(o2, o3);
        digraph2.addEdge(o3, o1);

        Assertions.assertThrows(GraphCycleException.class, () -> digraph2.topologicalSort());
    }
}
