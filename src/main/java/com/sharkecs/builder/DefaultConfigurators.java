package com.sharkecs.builder;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.function.Predicate;

import com.sharkecs.Archetype;
import com.sharkecs.Aspect;
import com.sharkecs.ComponentMapper;
import com.sharkecs.Engine;
import com.sharkecs.Initializable;
import com.sharkecs.Processor;
import com.sharkecs.Subscriber;
import com.sharkecs.Subscription;
import com.sharkecs.Transmutation;

/**
 * Container class of all default {@link Configurator}s used by
 * {@link EngineBuilder#withDefaults(int)}.
 * 
 * @author Joannick Gardize
 *
 */
public class DefaultConfigurators {

	private DefaultConfigurators() {
		throw new UnsupportedOperationException();
	}

	/**
	 * Lambda-style {@link Configurator} of {@link Subscriber}s, creates and bind
	 * {@link Subscription}s via the annotation-declared aspect of the subscriber
	 * type.
	 * 
	 * @param subscriber
	 * @param engineBuilder
	 */
	public static void configureSubscriber(Subscriber subscriber, EngineBuilder engineBuilder) {
		subscriber.subscribe(engineBuilder.getRegistrations().computeIfAbsent(Subscription.class,
		        new Aspect(subscriber.getClass()), () -> new Subscription(engineBuilder.getExpectedEntityCount())));
	}

	/**
	 * Lambda-style {@link Configurator} of {@link Archetype}s. Creates the arrays
	 * of {@link Subscription}, {@link ComponentMapper} and {@link Transmutation}
	 * related to the Archetype.
	 * 
	 * @param archetype
	 * @param engineBuilder
	 */
	@SuppressWarnings("unchecked")
	public static void configureArchetype(Archetype archetype, EngineBuilder engineBuilder) {
		RegistrationMap registrations = engineBuilder.getRegistrations();
		archetype.setSubscriptions(registrations.entrySet(Subscription.class).stream()
		        .filter(e -> ((Aspect) e.getKey()).matches(archetype.getComponentTypes())).map(Entry::getValue)
		        .toArray(Subscription[]::new));
		archetype.setComponentMappers(archetype.getComponentTypes().stream()
		        .map(t -> registrations.getOrFail(ComponentMapper.class, t)).toArray(ComponentMapper[]::new));
		archetype.setAutoCreateComponentMappers(archetype.getComponentTypes().stream()
		        .filter(t -> archetype.isAutoCreation(t, engineBuilder.defaultComponentAutoCreation()))
		        .map(t -> registrations.getOrFail(ComponentMapper.class, t)).toArray(ComponentMapper[]::new));
		archetype.setTransmutations(new Transmutation[registrations.typeCount(Archetype.class)]);
	}

	/**
	 * Lambda-style {@link Configurator} of {@link Processor}. They are added to the
	 * {@link Engine}, in their registration order.
	 * 
	 * @param processor
	 * @param engineBuilder
	 */
	public static void configureProcessors(Processor processor, EngineBuilder engineBuilder) {
		engineBuilder.addProcessor(processor);
	}

	/**
	 * Lambda-style {@link Configurator} of {@link Initializable}. Calling
	 * {@link Initializable#initialize()}, usually after injection.
	 * 
	 * @param initializable
	 * @param engineBuilder
	 */
	public static void configureInitializables(Initializable initializable, EngineBuilder engineBuilder) {
		initializable.initialize();
	}

	/**
	 * {@link Configurator} of {@link Transmutation}s. Computes the difference of
	 * {@link Subscription} and {@link ComponentMapper} between the "from" and the
	 * "to" {@link Archetype}, for a fast transmutation operation at runtime.
	 * 
	 * @author Joannick Gardize
	 *
	 */
	public static class TransmutationConfigurator implements Configurator<Transmutation> {

		private static class ArchetypeSets {
			Set<Subscription> subscriptions;
			@SuppressWarnings("rawtypes")
			Set<ComponentMapper> autoCreateComponentMappers;
			@SuppressWarnings("rawtypes")
			Set<ComponentMapper> componentMappers;

			public ArchetypeSets(Archetype archetype) {
				subscriptions = new HashSet<>(Arrays.asList(archetype.getSubscriptions()));
				autoCreateComponentMappers = new HashSet<>(Arrays.asList(archetype.getAutoCreateComponentMappers()));
				componentMappers = new HashSet<>(Arrays.asList(archetype.getComponentMappers()));
			}
		}

		private Map<Archetype, ArchetypeSets> archetypeSets = new HashMap<>();

		@Override
		@SuppressWarnings("unchecked")
		public void configure(Transmutation transmutation, EngineBuilder engineBuilder) {
			ArchetypeSets from = archetypeSets.computeIfAbsent(transmutation.getFrom(), ArchetypeSets::new);
			ArchetypeSets to = archetypeSets.computeIfAbsent(transmutation.getTo(), ArchetypeSets::new);

			transmutation.getFrom().getTransmutations()[transmutation.getTo().getId()] = transmutation;

			transmutation.setAddSubscriptions(notContains(Subscription.class, to.subscriptions, from.subscriptions));
			transmutation.setRemoveSubscriptions(notContains(Subscription.class, from.subscriptions, to.subscriptions));
			transmutation.setChangeSubscriptions(contains(Subscription.class, from.subscriptions, to.subscriptions));

			transmutation.setAddMappers(
			        notContains(ComponentMapper.class, to.autoCreateComponentMappers, from.componentMappers));
			transmutation
			        .setRemoveMappers(notContains(ComponentMapper.class, from.componentMappers, to.componentMappers));
		}

		private <T> T[] notContains(Class<T> elementType, Set<T> compared, Set<T> comparing) {
			return filterToArray(elementType, compared, e -> !comparing.contains(e));
		}

		private <T> T[] contains(Class<T> elementType, Set<T> compared, Set<T> comparing) {
			return filterToArray(elementType, compared, comparing::contains);
		}

		@SuppressWarnings("unchecked")
		private <T> T[] filterToArray(Class<T> elementType, Set<T> initial, Predicate<T> matcher) {
			return initial.stream().filter(matcher).toArray(size -> (T[]) Array.newInstance(elementType, size));
		}
	}
}
