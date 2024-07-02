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

package sharkhendrix.sharkecs.builder.configurator;

import sharkhendrix.sharkecs.subscription.Subscription;

import java.util.HashMap;
import java.util.Map;

public class SubscriptionGroup {
    private boolean requiresTracking;
    private Map<String, Subscription> subscriptionsBySort = new HashMap<>();

    public boolean isRequiresTracking() {
        return requiresTracking;
    }

    public void setRequiresTracking(boolean requiresTracking) {
        this.requiresTracking = requiresTracking;
    }

    public Map<String, Subscription> getSubscriptionsBySort() {
        return subscriptionsBySort;
    }
}
