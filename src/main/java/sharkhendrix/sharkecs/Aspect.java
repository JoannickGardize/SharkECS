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

import sharkhendrix.sharkecs.annotation.With;
import sharkhendrix.sharkecs.annotation.WithAny;
import sharkhendrix.sharkecs.annotation.Without;
import sharkhendrix.sharkecs.subscription.Subscriber;

import java.util.Objects;
import java.util.Set;
import java.util.function.BiPredicate;
import java.util.function.Predicate;
import java.util.stream.Stream;

/**
 * <p>
 * Represents a set of component composition of entities. This is not intended
 * to be instantiated by users. Instead, annotations should be used in classes
 * declaration to declare their interest to a given aspect. The annotated class
 * should implements {@link Subscriber} to be wired during the engine building.
 * <p>
 * There is three possible annotations:
 * <ul>
 * <li>{@link With}: contains at least all the given component types.
 * <li>{@link WithAny}: contains at least one of the given component types.
 * <li>{@link Without}: does not contains any of the given component types.
 * </ul>
 * These three annotations may be mixed.
 * <p>
 * If the given class does not have any of these annotations, the assumed Aspect
 * will match with all possible composition.
 */
public class Aspect {

    private Set<Class<?>> with;
    private Set<Class<?>> withAny;
    private Set<Class<?>> without;

    /**
     * Creates a new Aspect using annotations present on the given class.
     *
     * @param annotatedType the annotated class
     */
    public Aspect(Class<?> annotatedType) {
        With withAnnotation = annotatedType.getAnnotation(With.class);
        with = withAnnotation != null ? Set.of(withAnnotation.value()) : null;
        WithAny withAnyAnnotation = annotatedType.getAnnotation(WithAny.class);
        withAny = withAnyAnnotation != null ? Set.of(withAnyAnnotation.value()) : null;
        Without withoutAnnotation = annotatedType.getAnnotation(Without.class);
        without = withoutAnnotation != null ? Set.of(withoutAnnotation.value()) : null;
    }

    /**
     * Tests if this aspect matches the given set of component types.
     *
     * @param componentTypes the set of component types to test
     * @return true if this aspect matches with the given set, false otherwise.
     */
    public boolean matches(Set<Class<?>> componentTypes) {
        return isNullOr(with, componentTypes, Stream::allMatch)
                && isNullOr(withAny, componentTypes, Stream::anyMatch)
                && isNullOr(without, componentTypes, Stream::noneMatch);
    }

    private boolean isNullOr(Set<Class<?>> filterSet, Set<Class<?>> toTestSet, BiPredicate<Stream<Class<?>>, Predicate<Class<?>>> filter) {
        return filterSet == null || filter.test(filterSet.stream(), toTestSet::contains);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + Objects.hashCode(with);
        result = prime * result + Objects.hashCode(withAny);
        result = prime * result + Objects.hashCode(without);
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        } else if (!(obj instanceof Aspect)) {
            return false;
        }
        Aspect other = (Aspect) obj;
        return Objects.equals(with, other.with) && Objects.equals(withAny, other.withAny) && Objects.equals(without, other.without);
    }

}
