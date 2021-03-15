package com.sharkecs.builder.configurator;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.sharkecs.annotation.BeforeAll;
import com.sharkecs.builder.EngineBuilder;
import com.sharkecs.builder.EngineConfigurationException;
import com.sharkecs.builder.RegistrationMap;
import com.sharkecs.util.Digraph;
import com.sharkecs.util.GraphCycleException;

/**
 * <p>
 * A configurator able to order elements according to their priority
 * constraints. Internally uses a {@link Digraph} to resolve priority.
 * <p>
 * Priority constraint elements passed in {@link #before(Object, Object...)} and
 * {@link #after(Object, Object...)} could be:
 * <ul>
 * <li>the instance of a registered element, or any instance as "marker" in the
 * priority graph
 * <li>an object or interface type, all registration objects assignable to this
 * type will be concerned.
 * <li>an annotation type, all registration objects declaring this annotation in
 * their instance type will be concerned.
 * </ul>
 * <p>
 * {@link Prioritizer#configure(EngineBuilder)} must be called before calling
 * {@link Prioritizer#priorityOf(Object)} or
 * {@link Prioritizer#prioritize(List)}. Once
 * {@link Prioritizer#configure(EngineBuilder)} is called,
 * {@link Prioritizer#after(Object, Object...)} and
 * {@link Prioritizer#before(Object, Object...)} throws an error.
 * 
 * @author Joannick Gardize
 *
 */
@BeforeAll
public class Prioritizer implements Configurator {

	private static class PriorityEntry {

		Function<RegistrationMap, Collection<Object>> beforeFunction;
		Function<RegistrationMap, Collection<Object>> afterFunction;

		PriorityEntry(Function<RegistrationMap, Collection<Object>> beforeFunction, Function<RegistrationMap, Collection<Object>> afterFunction) {
			this.beforeFunction = beforeFunction;
			this.afterFunction = afterFunction;
		}

		void apply(Digraph<Object> graph, RegistrationMap registrations) {
			for (Object before : beforeFunction.apply(registrations)) {
				for (Object after : afterFunction.apply(registrations)) {
					if (before != after) {
						graph.addEdge(before, after);
					}
				}
			}
		}
	}

	private Map<Object, Integer> priorityMap;
	private List<PriorityEntry> priorityEntries = new ArrayList<>();
	private boolean configured = false;
	private Map<Class<? extends Annotation>, Collection<Object>> annotatedCache = new IdentityHashMap<>();

	/**
	 * Add a constraint between the {@code before} object / class / annotation to be
	 * before all {@code after} objects / classes.
	 * 
	 * @param before
	 * @param after
	 */
	public void before(Object before, Object... after) {
		checkNotConfigured();
		checkSelfPriority(before, after);
		for (Object afterElement : after) {
			priorityEntries.add(createPriorityEntry(before, afterElement));
		}
	}

	/**
	 * Add a constraint between the {@code after} object / class / annotation to be
	 * after all {@code before} objects / classes.
	 * 
	 * @param after
	 * @param before
	 */
	public void after(Object after, Object... before) {
		checkNotConfigured();
		checkSelfPriority(after, before);
		for (Object beforeElement : before) {
			priorityEntries.add(createPriorityEntry(beforeElement, after));
		}
	}

	@Override
	public void configure(EngineBuilder engineBuilder) {
		buildPriorityMap(createPriorityGraph(engineBuilder));
		configured = true;
	}

	/**
	 * Returns the computed priority value of the given object.
	 * {@link #configure(EngineBuilder)} must be called before calling this
	 * function.
	 * 
	 * @param object
	 * @return the priority of the given object (lower values has more priority)
	 */
	public int priorityOf(Object object) {
		if (!configured) {
			throw new EngineConfigurationException("Priority not configured yet");
		}
		Integer priority = priorityMap.get(object);
		if (priority != null) {
			return priority;
		} else {
			priority = priorityMap.get(object.getClass());
			return priority != null ? priority : Integer.MAX_VALUE;
		}
	}

	/**
	 * Sort the given list from the most priority object to the lowest.
	 * {@link #configure(EngineBuilder)} must be called before calling this
	 * function.
	 * 
	 * @param list
	 */
	public void prioritize(List<?> list) {
		list.sort((o1, o2) -> Integer.compare(priorityOf(o1), priorityOf(o2)));
	}

	private PriorityEntry createPriorityEntry(Object before, Object after) {
		return new PriorityEntry(createPriorityFunction(before), createPriorityFunction(after));
	}

	@SuppressWarnings("unchecked")
	private Function<RegistrationMap, Collection<Object>> createPriorityFunction(Object object) {
		if (object instanceof Class) {
			if (((Class<?>) object).isAnnotation()) {
				return r -> annotatedCache.computeIfAbsent((Class<? extends Annotation>) object,
						a -> r.all().stream().filter(o -> o.getClass().isAnnotationPresent(a)).collect(Collectors.toList()));
			} else {
				return r -> r.getAllAssignableFrom((Class<Object>) object);
			}
		} else {
			return r -> Collections.singleton(object);
		}
	}

	private Digraph<Object> createPriorityGraph(EngineBuilder engineBuilder) {
		Digraph<Object> priorityGraph = new Digraph<>();
		for (PriorityEntry entry : priorityEntries) {
			entry.apply(priorityGraph, engineBuilder.getRegistrations());
		}
		return priorityGraph;
	}

	private void buildPriorityMap(Digraph<Object> graph) {
		priorityMap = new IdentityHashMap<>();
		try {
			List<Object> depths = graph.topologicalSort();
			for (int i = 0; i < depths.size(); i++) {
				priorityMap.put(depths.get(i), i);
			}
		} catch (GraphCycleException e) {
			throw new EngineConfigurationException("Error occured during priority computation", e);
		}
	}

	private void checkSelfPriority(Object single, Object... array) {
		for (Object obj : array) {
			if (Objects.equals(single, obj)) {
				throw new EngineConfigurationException("Cannot add a priority to itself");
			}
		}
	}

	private void checkNotConfigured() {
		if (configured) {
			throw new EngineConfigurationException("Cannot add a priority once configured");
		}
	}
}
