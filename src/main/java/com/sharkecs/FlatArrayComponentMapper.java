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

import com.sharkecs.util.Bag;

import java.util.function.Supplier;

/**
 * Simplest and fastest implementation of {@link ComponentMapper}, but has the
 * worst memory print, especially for components that are rare among all
 * entities.
 *
 * @param <T> the component type
 * @author Joannick Gardize
 */
public class FlatArrayComponentMapper<T> implements ComponentMapper<T> {

    private Bag<T> components;
    private Supplier<? extends T> newInstanceSupplier;

    public FlatArrayComponentMapper(int initialCapacity, Supplier<? extends T> newInstanceSupplier) {
        components = new Bag<>(initialCapacity);
        this.newInstanceSupplier = newInstanceSupplier;
    }

    @Override
    public T create(int entityId) {
        T component = newInstanceSupplier.get();
        components.put(entityId, component);
        return component;
    }

    @Override
    public void put(int entityId, T component) {
        components.put(entityId, component);
    }

    @Override
    public void remove(int entityId) {
        components.unsafeSet(entityId, null);
    }

    @Override
    public T get(int entityId) {
        return components.get(entityId);
    }

    @Override
    public T getIfExists(int entityId) {
        return components.getOrNull(entityId);
    }

}
