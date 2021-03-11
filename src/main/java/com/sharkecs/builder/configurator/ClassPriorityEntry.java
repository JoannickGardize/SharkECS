package com.sharkecs.builder.configurator;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;

abstract class ClassPriorityEntry {

	public static ClassPriorityEntry of(Object before, Object after) {
		if (before instanceof Class && after instanceof Class) {
			return new ClassToClass((Class<?>) before, (Class<?>) after);
		} else if (before instanceof Class) {
			return new ClassToObject((Class<?>) before, after);
		} else if (after instanceof Class) {
			return new ObjectToClass(before, (Class<?>) after);
		} else {
			throw new IllegalArgumentException();
		}
	}

	private static class ClassToClass extends ClassPriorityEntry {
		private Class<?> from;
		private Class<?> to;

		public ClassToClass(Class<?> from, Class<?> to) {
			this.from = from;
			this.to = to;
		}

		@Override
		public Collection<Class<?>> getClasses() {
			return Arrays.asList(from, to);
		}

		@Override
		public void configure(Prioritizer prioritizer, Map<Class<?>, Collection<Object>> objectsByClass) {
			objectsByClass.get(from).forEach(objectFrom -> objectsByClass.get(to).forEach(objectTo -> beforeIfNotSame(prioritizer, objectFrom, objectTo)));
		}
	}

	private static class ObjectToClass extends ClassPriorityEntry {
		private Object from;
		private Class<?> to;

		public ObjectToClass(Object from, Class<?> to) {
			this.from = from;
			this.to = to;
		}

		@Override
		public Collection<Class<?>> getClasses() {
			return Collections.singleton(to);
		}

		@Override
		public void configure(Prioritizer prioritizer, Map<Class<?>, Collection<Object>> objectsByClass) {
			objectsByClass.get(to).forEach(objectTo -> beforeIfNotSame(prioritizer, from, objectTo));
		}
	}

	private static class ClassToObject extends ClassPriorityEntry {
		private Class<?> from;
		private Object to;

		public ClassToObject(Class<?> from, Object to) {
			this.from = from;
			this.to = to;
		}

		@Override
		public Collection<Class<?>> getClasses() {
			return Collections.singleton(from);
		}

		@Override
		public void configure(Prioritizer prioritizer, Map<Class<?>, Collection<Object>> objectsByClass) {
			objectsByClass.get(from).forEach(objectFrom -> beforeIfNotSame(prioritizer, objectFrom, to));
		}
	}

	private static void beforeIfNotSame(Prioritizer prioritizer, Object from, Object to) {
		if (from != to) {
			prioritizer.before(from, to);
		}
	}

	public abstract Collection<Class<?>> getClasses();

	public abstract void configure(Prioritizer prioritizer, Map<Class<?>, Collection<Object>> objectsByClass);
}
