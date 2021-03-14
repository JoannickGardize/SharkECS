package com.sharkecs.annotation;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import com.sharkecs.Aspect;
import com.sharkecs.Subscriber;

/**
 * Annotation to build the {@link Aspect} interest of a {@link Subscriber}
 * class. Entities must not have any f the given component types to match the
 * aspect.
 * 
 * @author Joannick Gardize
 *
 */
@Retention(RUNTIME)
@Target(TYPE)
public @interface Without {
	Class<?>[] value();
}
