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

package sharkhendrix.sharkecs.util;

import sharkhendrix.sharkecs.testutil.TestUtil;
import org.junit.jupiter.api.Test;

class SortableIntArrayListTest {


    @Test
    void test() {
        int[] order = new int[]{0, 1, 2, 3, 4};

        SortableIntArrayList list = new SortableIntArrayList(10, (e1, e2) -> order[e1] - order[e2]);

        list.add(3);
        list.add(1);
        list.add(2);
        list.add(0);
        list.add(4);

        IntIterator it = list.iterator();
        TestUtil.assertIterator(it, 0, 1, 2, 3, 4);

        order[0] = 1;
        order[1] = 0;
        order[2] = 3;
        order[3] = 4;
        order[4] = 2;
        list.sort();
        it.reset();
        TestUtil.assertIterator(it, 1, 0, 4, 2, 3);
        list.remove(4);
        it.reset();
        TestUtil.assertIterator(it, 1, 0, 2, 3);
        list.remove(0);
        list.remove(1);
        list.remove(2);
        list.remove(3);
        TestUtil.assertIterator(it);
    }


}