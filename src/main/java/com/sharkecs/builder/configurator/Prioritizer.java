package com.sharkecs.builder.configurator;

import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

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
 * Priority constraints can be defined by using object instances or by using
 * classes, affecting all objects assignable from this class.
 * <p>
 * {@link Prioritizer#configure(EngineBuilder)} must be called before calling
 * {@link Prioritizer#priorityOf(Object)} or
 * {@link Prioritizer#prioritize(List)}. Once
 * {@link Prioritizer#configure(EngineBuilder)} is called,
 * {@link Prioritizer#after(Object, Object...)} and
 * {@link Prioritizer#before(Object, Object...)} has no effect.
 * 
 * @author Joannick Gardize
 *
 */
@BeforeAll
public class Prioritizer implements Configurator {

	private static interface ClassPriorityEntry {
		void configure(RegistrationMap registrations);
	}

	private Digraph<Object> priorityGraph = new Digraph<>();
	private Map<Object, Integer> priorityMap;
	private List<ClassPriorityEntry> classPriorityEntries = new ArrayList<>();

	/**
	 * Add a constraint between the {@code before} object / class to be before all
	 * {@code after} objects / classes.
	 * 
	 * @param before
	 * @param after
	 */
	public void before(Object before, Object... after) {
		checkSelfPriority(before, after);
		if (before instanceof Class) {
			for (Object afterElement : after) {
				classPriorityEntries.add(createClassPriorityEntry(before, afterElement));
			}
		} else {
			for (Object afterElement : after) {
				if (afterElement instanceof Class) {
					classPriorityEntries.add(createClassPriorityEntry(before, afterElement));
				} else {
					priorityGraph.precedes(before, afterElement);
				}
			}
		}
	}

	private ClassPriorityEntry createClassPriorityEntry(Object before, Object after) {
		if (before instanceof Class && after instanceof Class) {
			return registrations -> registrations.forEachAssignableFrom((Class<?>) before,
					fromObj -> registrations.forEachAssignableFrom((Class<?>) after, toObj -> beforeIfNotSame(fromObj, toObj)));
		} else if (before instanceof Class) {
			return registrations -> registrations.forEachAssignableFrom((Class<?>) before, fromObj -> beforeIfNotSame(fromObj, after));
		} else if (after instanceof Class) {
			return registrations -> registrations.forEachAssignableFrom((Class<?>) after, afterObj -> beforeIfNotSame(before, afterObj));
		} else {
			throw new IllegalArgumentException();
		}
	}

	/**
	 * Add a constraint between the {@code after} object / class to be after all
	 * {@code before} objects / classes.
	 * 
	 * @param after
	 * @param before
	 */
	public void after(Object after, Object... before) {
		checkSelfPriority(after, before);
		priorityGraph.follows(after, before);
		if (after instanceof Class) {
			for (Object beforeElement : before) {
				classPriorityEntries.add(createClassPriorityEntry(beforeElement, after));
			}
		} else {
			for (Object beforeElement : before) {
				if (beforeElement instanceof Class) {
					classPriorityEntries.add(createClassPriorityEntry(beforeElement, after));
				} else {
					priorityGraph.precedes(beforeElement, after);
				}
			}
		}
	}

	@Override
	public void configure(EngineBuilder engineBuilder) {
		applyClassPriorities(engineBuilder);
		buildPriorityMap();
	}

	/**
	 * Returns the computed priority value of the given object.
	 * 
	 * @param object
	 * @return the priority of the given object (lower values has more priority)
	 */
	public int priorityOf(Object object) {
		if (priorityMap == null) {
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
	 * 
	 * @param list
	 */
	public void prioritize(List<?> list) {
		list.sort((o1, o2) -> Integer.compare(priorityOf(o1), priorityOf(o2)));
	}

	private void checkSelfPriority(Object single, Object... array) {
		for (Object obj : array) {
			if (Objects.equals(single, obj)) {
				throw new EngineConfigurationException("Cannot add a priority to itself");
			}
		}
	}

	private void applyClassPriorities(EngineBuilder engineBuilder) {
		for (ClassPriorityEntry entry : classPriorityEntries) {
			entry.configure(engineBuilder.getRegistrations());
		}
	}

	private void buildPriorityMap() {
		priorityMap = new IdentityHashMap<>();
		Set<Object> remaining = buildValuesSet();
		try {
			while (!remaining.isEmpty()) {
				Object startingPoint = remaining.iterator().next();
				Map<Object, Integer> depths = priorityGraph.computeDepth(startingPoint);
				priorityMap.putAll(depths);
				remaining.removeAll(depths.keySet());
			}
		} catch (GraphCycleException e) {
			throw new EngineConfigurationException("Error occured during priority computation", e);
		}
	}

	private Set<Object> buildValuesSet() {
		Map<Object, Object> map = new IdentityHashMap<>();
		priorityGraph.values().forEach(o -> map.put(o, o));
		return map.keySet();
	}

	private void beforeIfNotSame(Object from, Object to) {
		if (from != to) {
			before(from, to);
		}
	}
}
