package com.sharkecs.annotation;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import com.sharkecs.builder.configurator.Injector;

/**
 * <p>
 * Annotation for type or field to indicates to the {@link Injector} that
 * injection is required.
 * <p>
 * When annotating a class, it indicates that all fields requires injection,
 * unless they are marked with {@link SkipInjection}.
 * <p>
 * the {@link #injectParent()} boolean can be set to true to indicates to check
 * the parent class for injection, with the same rules.
 * 
 * @author Joannick Gardize
 *
 */
@Retention(RUNTIME)
@Target({ TYPE, FIELD })
public @interface Inject {
	boolean injectParent() default false;
}
