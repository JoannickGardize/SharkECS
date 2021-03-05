package com.sharkecs;

public interface SubscriptionListener {

    void added(int entityId);

    void removed(int entityId);

    void changed(int entityId);
}
