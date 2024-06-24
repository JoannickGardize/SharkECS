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

package com.sharkecs;

import com.sharkecs.testutil.BagTestUtils;
import com.sharkecs.testutil.SubscriptionLogger;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class TrackingSubscriptionTest {

    @Test
    void test() {
        SubscriptionLogger listener = new SubscriptionLogger();

        Subscription subscription = new TrackingSubscription(10);
        subscription.addListener(listener);

        Assertions.assertTrue(subscription.getEntities().isEmpty());

        subscription.add(3);
        subscription.add(5);
        subscription.add(7);

        BagTestUtils.assertBagEqualsAnyOrder(subscription.getEntities(), 3, 5, 7);

        subscription.remove(5);
        subscription.remove(7);
        subscription.add(11);
        subscription.add(12);

        BagTestUtils.assertBagEqualsAnyOrder(subscription.getEntities(), 3, 11, 12);

        listener.assertAddLog(3, 5, 7, 11, 12);
        listener.assertRemoveLog(5, 7);
    }
}
