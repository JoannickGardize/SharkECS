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

import sharkhendrix.sharkecs.util.Bag;

import java.util.function.Supplier;

/**
 * Simplest and fastest implementation of {@link ComponentMapper}, but has the
 * worst memory print, especially for components that are rare among all
 * entities.
 *
 * @param <T> the component type
 */
public class ArrayComponentMapper<T> implements ComponentMapper<T> {

    private Bag<T> components;
    private Supplier<? extends T> newInstanceSupplier;

    public ArrayComponentMapper(int initialCapacity, Supplier<? extends T> newInstanceSupplier) {
        components = new Bag<>(initialCapacity);
        this.newInstanceSupplier = newInstanceSupplier;
    }

    @Override
    public T create(int entity) {
        T component = newInstanceSupplier.get();
        components.put(entity, component);
        return component;
    }

    @Override
    public void put(int entity, T component) {
        components.put(entity, component);
    }

    @Override
    public void remove(int entity) {
        components.unsafeSet(entity, null);
    }

    @Override
    public T get(int entity) {
        return components.get(entity);
    }

    @Override
    public T getIfExists(int entity) {
        return components.getOrNull(entity);
    }

}
