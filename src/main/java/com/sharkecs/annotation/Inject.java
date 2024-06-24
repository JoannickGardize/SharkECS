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

import com.sharkecs.builder.configurator.Injector;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * <p>
 * Annotation for type or field to indicates to the {@link Injector} that
 * injection is required.
 * <p>
 * When annotating a class, it indicates that all fields requires injection,
 * unless they are marked with {@link SkipInject}.
 * <p>
 * the {@link #injectParent()} boolean can be set to true to indicates to check
 * the parent class for injection, with the same rules.
 *
 * @author Joannick Gardize
 */
@Retention(RUNTIME)
@Target({TYPE, FIELD})
public @interface Inject {
    boolean injectParent() default false;
}
