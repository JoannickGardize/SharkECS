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

package com.sharkecs.annotation;

import com.sharkecs.Aspect;
import com.sharkecs.Subscriber;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Annotation to build the {@link Aspect} interest of a {@link Subscriber}
 * class. Entities must not have any f the given component types to match the
 * aspect.
 *
 * @author Joannick Gardize
 */
@Retention(RUNTIME)
@Target(TYPE)
public @interface Without {
    Class<?>[] value();
}
