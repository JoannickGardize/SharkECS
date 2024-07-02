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

import sharkhendrix.sharkecs.annotation.CreationPolicy;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class ArchetypeTest {

    static class A {

    }

    @CreationPolicy(Archetype.ComponentCreationPolicy.MANUAL)
    static class B {

    }

    @Test
    void getComponentCreationPolicySTest() {

        Archetype archetype = new Archetype("test", A.class, B.class);

        Assertions.assertEquals(Archetype.ComponentCreationPolicy.MANUAL, archetype.getComponentCreationPolicy(A.class, Archetype.ComponentCreationPolicy.MANUAL));
        Assertions.assertEquals(Archetype.ComponentCreationPolicy.MANUAL, archetype.getComponentCreationPolicy(B.class, Archetype.ComponentCreationPolicy.AUTOMATIC));
        archetype.setComponentCreationPolicy(Archetype.ComponentCreationPolicy.MANUAL, A.class);
        Assertions.assertEquals(Archetype.ComponentCreationPolicy.MANUAL, archetype.getComponentCreationPolicy(A.class, Archetype.ComponentCreationPolicy.AUTOMATIC));
        archetype.setComponentCreationPolicy(Archetype.ComponentCreationPolicy.AUTOMATIC);
        Assertions.assertEquals(Archetype.ComponentCreationPolicy.AUTOMATIC, archetype.getComponentCreationPolicy(B.class, Archetype.ComponentCreationPolicy.MANUAL));
    }
}
