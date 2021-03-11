package com.sharkecs.builder.configurator;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;

import com.sharkecs.Archetype;
import com.sharkecs.ComponentMapper;
import com.sharkecs.Subscription;
import com.sharkecs.Transmutation;
import com.sharkecs.builder.EngineBuilder;

/**
 * {@link Configurator} of {@link Transmutation}s. Computes the difference of
 * {@link Subscription} and {@link ComponentMapper} between the "from" and the
 * "to" {@link Archetype}, for a fast transmutation operation at runtime.
 * 
 * @author Joannick Gardize
 */
public class TransmutationConfigurator extends TypeConfigurator<Transmutation> {

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

	public TransmutationConfigurator() {
		super(Transmutation.class);
	}

	@Override
	@SuppressWarnings("unchecked")
	protected void configure(Transmutation transmutation, EngineBuilder engineBuilder) {
		ArchetypeSets from = archetypeSets.computeIfAbsent(transmutation.getFrom(), ArchetypeSets::new);
		ArchetypeSets to = archetypeSets.computeIfAbsent(transmutation.getTo(), ArchetypeSets::new);

		transmutation.getFrom().getTransmutations()[transmutation.getTo().getId()] = transmutation;

		transmutation.setAddSubscriptions(notContains(Subscription.class, to.subscriptions, from.subscriptions));
		transmutation.setRemoveSubscriptions(notContains(Subscription.class, from.subscriptions, to.subscriptions));
		transmutation.setChangeSubscriptions(contains(Subscription.class, from.subscriptions, to.subscriptions));

		transmutation.setAddMappers(notContains(ComponentMapper.class, to.autoCreateComponentMappers, from.componentMappers));
		transmutation.setRemoveMappers(notContains(ComponentMapper.class, from.componentMappers, to.componentMappers));
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
