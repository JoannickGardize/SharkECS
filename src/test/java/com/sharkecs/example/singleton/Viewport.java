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

package com.sharkecs.example.singleton;

/**
 * Represents the currently viewing portion of a one-dimensional world.
 *
 * @author Joannick Gardize
 */
public class Viewport {

    private int start;
    private int end;

    /**
     * @return the starting position of the viewport, inclusive
     */
    public int getStart() {
        return start;
    }

    public void setStart(int start) {
        this.start = start;
    }

    /**
     * @return the end position of the viewport, exclusive
     */
    public int getEnd() {
        return end;
    }

    public void setEnd(int end) {
        this.end = end;
    }
}
