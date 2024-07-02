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

package sharkhendrix.sharkecs;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class ArrayComponentMapperTest {

    private Object component;

    @Test
    void test() {
        ComponentMapper<Object> mapper = new ArrayComponentMapper<>(5, Object::new);
        mapper.create(2);
        mapper.create(4);
        mapper.create(10);

        Assertions.assertNotNull(mapper.getIfExists(2));
        Assertions.assertNotNull(mapper.get(4));
        Assertions.assertNotNull(mapper.get(10));
        Assertions.assertNotSame(mapper.get(4), mapper.get(10));

        mapper.remove(2);
        Assertions.assertNull(mapper.get(2));
        Assertions.assertNull(mapper.getIfExists(2));
        Assertions.assertNotNull(mapper.get(4));
        Assertions.assertNull(mapper.getIfExists(1000));

        component = false;
        mapper.ifExists(10, c -> component = c);
        Assertions.assertSame(mapper.get(10), component);
        mapper.ifExists(2, c -> Assertions.fail("action must not be executed"));
    }
}
