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

package com.sharkecs.builder.configurator;

import com.sharkecs.annotation.ForceInject;
import com.sharkecs.annotation.Inject;
import com.sharkecs.annotation.SkipInject;
import com.sharkecs.builder.EngineBuilder;
import com.sharkecs.builder.EngineConfigurationException;
import com.sharkecs.builder.RegistrationMap;
import com.sharkecs.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.HashSet;
import java.util.Set;

/**
 * <p>
 * Configurator wiring the eligible fields of registered objects of a
 * {@link RegistrationMap} between them.
 * <p>
 * A field is eligible if one of the following condition is met:
 * <ul>
 * <li>The declaring class is annotated with {@link Inject}
 * <li>The declaring class is assignable to one of the registered auto inject
 * type via {@link #addAutoInjectType(Class)}
 * <li>The field is annotated with {@link Inject}
 * </ul>
 * If the field or its declaring class is annotated with {@link SkipInject},
 * injection will always be skipped.
 * <p>
 * When a field is eligible, the injector will look the {@link RegistrationMap}
 * for an adequate object. If there is objects registered with the exact same
 * type (no <i>is assignable from</i> check), it will choose one by checking its
 * registration keys by following these rules, in this priority order:
 * <ul>
 * <li>a String key exactly matching the field name
 * <li>a Class key matching the first generic type of the field's type
 * <li>the empty key (null)
 * </ul>
 * If no registration has been found at this point, and
 * {@code injectAnyAssignableType} has been set to true, via
 * {@link #setInjectAnyAssignableType(boolean)}, any registration assignable
 * from the field's type will be taken, if any.
 * <p>
 * If a field is eligible but no registered object has been found for it,
 * nothing happens. This can be changed by
 * {@link Injector#setFailWhenNotFound(boolean)}, throwing an
 * {@link EngineConfigurationException} if set to true.
 * <p>
 * Injection is done via setter methods, if a field is eligible and a registered
 * object has been found for it, but the setter method is missing or not
 * visible, an {@link EngineConfigurationException} is thrown.
 * <p>
 * By default, fields of the parent class are not injected, use
 * {@link Inject#injectParent()} on the class to change that, or use
 * {@link ForceInject} to parent classes / fields to force their injections.
 *
 * @author Joannick Gardize
 */
public class Injector implements Configurator {

    private Set<Class<?>> autoInjectTypes = new HashSet<>();
    private boolean failWhenNotFound = false;
    private boolean injectAnyAssignableType = false;

    /**
     * Adds the given type as auto-inject type. Objects assignable from this type
     * will be injected without the need of marking them with {@link Inject}.
     *
     * @param type the auto-inject type to add
     */
    public void addAutoInjectType(Class<?> type) {
        autoInjectTypes.add(type);
    }

    public void removeAutoInjectType(Class<?> type) {
        autoInjectTypes.remove(type);
    }

    /**
     * Set if this injector should fail with an {@link EngineConfigurationException}
     * when no registration object is found for an eligible field. False by default.
     *
     * @param failWhenNotFound if true, injection will fail with an
     *                         {@link EngineConfigurationException} when no object
     *                         is found for an eligible field.
     */
    public void setFailWhenNotFound(boolean failWhenNotFound) {
        this.failWhenNotFound = failWhenNotFound;
    }

    /**
     * Set if this injector should try to inject any registration object by
     * assignable type when no matching registration object has been found by key
     * and type. False by default.
     *
     * @param injectAnyAssignableType if true, when no matching key has been found
     *                                (including the null key for the field's type)
     *                                any assignable type for the field will be
     *                                taken, if any.
     */
    public void setInjectAnyAssignableType(boolean injectAnyAssignableType) {
        this.injectAnyAssignableType = injectAnyAssignableType;
    }

    @Override
    public void configure(EngineBuilder engineBuilder) {
        engineBuilder.getRegistrations().forEach(o -> inject(o, engineBuilder.getRegistrations()));
    }

    /**
     * Inject the eligible fields of the given object, using the given registration
     * map.
     *
     * @param object        the object to inject its eligible fields
     * @param registrations the registration map to use for injection
     */
    public void inject(Object object, RegistrationMap registrations) {
        inject(object.getClass(), object, registrations, false);
    }

    private void inject(Class<?> type, Object object, RegistrationMap registrations, boolean requiresForce) {
        if (type == null || type.isAnnotationPresent(SkipInject.class)) {
            return;
        }
        Inject inject = type.getAnnotation(Inject.class);
        inject(type.getSuperclass(), object, registrations, requiresForce || inject == null || !inject.injectParent());
        boolean injectAllFields = isAutoInjectType(type, requiresForce);
        for (Field field : type.getDeclaredFields()) {
            if (!isEligibleField(field, injectAllFields, requiresForce)) {
                continue;
            }
            if ((registrations.typeCount(field.getType()) == 0
                    || !injectByName(object, field, registrations) && !injectByGenericType(object, field, registrations) && !injectByKey(object, field, null, registrations))
                    && (!injectAnyAssignableType || !injectByAssignableType(object, field, registrations)) && failWhenNotFound) {
                throw new EngineConfigurationException("No registered object found for field " + field);
            }
        }
    }

    private boolean injectByName(Object object, Field field, RegistrationMap registrations) {
        return injectByKey(object, field, field.getName(), registrations);
    }

    private boolean injectByGenericType(Object object, Field field, RegistrationMap registrations) {
        Class<?> argumentType = ReflectionUtils.getFirstGenericTypeArgument(field.getGenericType());
        if (argumentType != null) {
            return injectByKey(object, field, argumentType, registrations);
        } else {
            return false;
        }
    }

    private boolean injectByAssignableType(Object object, Field field, RegistrationMap registrations) {
        return inject(object, field, registrations.getAnyAssignableFrom(field.getType()));
    }

    private boolean injectByKey(Object object, Field field, Object key, RegistrationMap registrations) {
        return inject(object, field, registrations.get(field.getType(), key));
    }

    private boolean inject(Object object, Field field, Object value) {
        if (value != null) {
            try {
                ReflectionUtils.getSetter(field).invoke(object, value);
                return true;
            } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException |
                     NoSuchMethodException e) {
                throw new EngineConfigurationException("Missing public setter to inject the field " + field, e);
            }
        } else {
            return false;
        }
    }

    private boolean isEligibleField(Field field, boolean injectAllFields, boolean requiresForce) {
        return !field.isAnnotationPresent(SkipInject.class)
                && (injectAllFields || (requiresForce ? field.isAnnotationPresent(ForceInject.class) : field.isAnnotationPresent(Inject.class)));
    }

    private boolean isAutoInjectType(Class<?> type, boolean requiresForce) {
        return type.isAnnotationPresent(ForceInject.class)
                || !requiresForce && (type.isAnnotationPresent(Inject.class) || autoInjectTypes.stream().anyMatch(t -> t.isAssignableFrom(type)));
    }
}
