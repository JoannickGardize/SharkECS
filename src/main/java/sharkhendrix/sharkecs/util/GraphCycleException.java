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

import java.util.Arrays;

public class GraphCycleException extends Exception {

    private static final long serialVersionUID = 1L;

    private final transient Object[] cyclePath;

    public GraphCycleException(Object... cyclePath) {
        super("Cycle found in the graph: " + Arrays.toString(cyclePath));
        this.cyclePath = cyclePath;
    }

    public Object[] getCyclePath() {
        return cyclePath;
    }
}
