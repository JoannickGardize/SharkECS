package com.sharkecs.annotation;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import com.sharkecs.Archetype.ComponentCreationPolicy;

/**
 * Annotation for component types, to indicate the preferred
 * {@link ComponentCreationPolicy}.
 * 
 * @author Joannick Gardize
 *
 */
@Retention(RUNTIME)
@Target(TYPE)
public @interface CreationPolicy {

	ComponentCreationPolicy value();
}
