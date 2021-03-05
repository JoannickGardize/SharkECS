package com.sharkecs;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.sharkecs.testutil.BagTestUtils;
import com.sharkecs.testutil.SubscriptionLogger;

class SubscriptionTest {

    @Test
    void test() {
        SubscriptionLogger listener = new SubscriptionLogger();

        Subscription subscription = new Subscription(10);
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
