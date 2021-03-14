package com.sharkecs.annotation;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import com.sharkecs.Subscriber;

/**
 * <p>
 * Optimization topic.
 * <p>
 * Annotation for {@link Subscriber} implementers to indicate if the subscriber
 * requires entity tracking (maintain an entity collection of the subscription).
 * <p>
 * True by default when this annotation is not present.
 * <p>
 * Use it with a false value when a subscriber only requires to get notified, to
 * avoid the maintenance of the unused entity collection.
 * 
 * @author Joannick Gardize
 *
 */
@Retention(RUNTIME)
@Target(TYPE)
public @interface RequiresEntityTracking {
	boolean value() default true;
}
