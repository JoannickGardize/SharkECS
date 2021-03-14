package com.sharkecs;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import com.sharkecs.annotation.WithAll;
import com.sharkecs.annotation.WithAny;
import com.sharkecs.annotation.Without;

/**
 * <p>
 * Represents a set of component composition of entities. This is not intended
 * to be instantiated by users. Instead, annotations should be used in classes
 * declaration to declare their interest to a given aspect. The annotated class
 * should implements {@link Subscriber} to be wired during the engine building.
 * <p>
 * There is three possible annotations:
 * <ul>
 * <li>{@link WithAll}: contains at least all the given component types.
 * <li>{@link WithAny}: contains at least one of the given component types.
 * <li>{@link Without}: does not contains any of the given component types.
 * </ul>
 * These three annotations may be mixed. If the given class does not have any of
 * these three annotations, the assumed Aspect will match with all possible
 * composition.
 * 
 * @author Joannick Gardize
 *
 */
public class Aspect {

	private Set<Class<?>> withAll;
	private Set<Class<?>> withAny;
	private Set<Class<?>> without;

	/**
	 * Creates a new Aspect using annotations present on the given class.
	 * 
	 * @param annotatedType the annotated class
	 */
	public Aspect(Class<?> annotatedType) {
		WithAll withAllAnnotation = annotatedType.getAnnotation(WithAll.class);
		withAll = withAllAnnotation != null ? new HashSet<>(Arrays.asList(withAllAnnotation.value())) : null;
		WithAny withAnyAnnotation = annotatedType.getAnnotation(WithAny.class);
		withAny = withAnyAnnotation != null ? new HashSet<>(Arrays.asList(withAnyAnnotation.value())) : null;
		Without withoutAnnotation = annotatedType.getAnnotation(Without.class);
		without = withoutAnnotation != null ? new HashSet<>(Arrays.asList(withoutAnnotation.value())) : null;
	}

	/**
	 * Tests if this aspect matches the given set of component types.
	 * 
	 * @param componentTypes the set of component types to test
	 * @return true if this aspect matches with the given set, false otherwise.
	 */
	public boolean matches(Set<Class<?>> componentTypes) {
		if (withAll != null) {
			for (Class<?> type : withAll) {
				if (!componentTypes.contains(type)) {
					return false;
				}
			}
		}
		if (without != null) {
			for (Class<?> type : without) {
				if (componentTypes.contains(type)) {
					return false;
				}
			}
		}
		if (withAny != null) {
			return anyMatches(componentTypes);
		}
		return true;
	}

	private boolean anyMatches(Set<Class<?>> componentTypes) {
		for (Class<?> type : withAny) {
			if (componentTypes.contains(type)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Objects.hashCode(withAll);
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
		return Objects.equals(withAll, other.withAll) && Objects.equals(withAny, other.withAny) && Objects.equals(without, other.without);
	}

}
