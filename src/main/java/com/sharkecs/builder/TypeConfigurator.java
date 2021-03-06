package com.sharkecs.builder;

class TypeConfigurator<T> {
	private Class<T> type;
	private Configurator<T> configurator;

	public TypeConfigurator(Class<T> type, Configurator<T> configurator) {
		this.type = type;
		this.configurator = configurator;
	}

	@SuppressWarnings("unchecked")
	public void configure(Object o, EngineBuilder engineBuilder) {
		if (type.isAssignableFrom(o.getClass())) {
			configurator.configure((T) o, engineBuilder);
		}
	}
}
