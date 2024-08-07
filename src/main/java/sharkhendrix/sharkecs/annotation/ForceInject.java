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

package sharkhendrix.sharkecs.annotation;

import sharkhendrix.sharkecs.builder.configurator.Injector;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Annotation for field or class to indicates to the {@link Injector} that the
 * annotated field or class must be injected, even if this is a superclass and
 * {@link Inject#injectParent()} is missing or not set to true on all
 * subclasses.
 */
@Retention(RUNTIME)
@Target({TYPE, FIELD})
public @interface ForceInject {
    boolean injectParent() default false;
}