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

package sharkhendrix.sharkecs.example.component;

public class Health {

    private int maximum;
    private int value;

    public void initialize(int maximum) {
        this.maximum = maximum;
        value = maximum;
    }

    public void takeDamage(int amount) {
        value -= amount;
        ensurePositiveValue();
    }

    public int getMaximum() {
        return maximum;
    }

    public int getValue() {
        return value;
    }

    private void ensurePositiveValue() {
        if (value < 0) {
            value = 0;
        }
    }

}
