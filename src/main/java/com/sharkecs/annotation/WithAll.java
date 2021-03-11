package com.sharkecs.annotation;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import com.sharkecs.Aspect;
import com.sharkecs.Subscriber;

/**
 * Annotation to build the {@link Aspect} insterest of a {@link Subscriber}
 * class.
 * 
 * @author Joannick Gardize
 *
 */
@Retention(RUNTIME)
@Target(TYPE)
public @interface WithAll {
	Class<?>[] value();
}
