package com.sharkecs.builder;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import com.sharkecs.Archetype;
import com.sharkecs.ComponentMapper;
import com.sharkecs.Engine;
import com.sharkecs.EntityManager;
import com.sharkecs.FlatArrayComponentMapper;
import com.sharkecs.Initializable;
import com.sharkecs.Processor;
import com.sharkecs.Subscriber;
import com.sharkecs.Transmutation;

/**
 * <p>
 * Builder of {@link Engine}. Uses {@link #withDefaults()} to create a builder
 * with the following minimal default configuration:
 * <ul>
 * <li>Adds all {@link Configurator} methods and classes from
 * {@link DefaultConfigurators} (See the javadoc of {@link DefaultConfigurators}
 * for more details about these configurators), plus the {@link Injector}.
 * <li>Adds {@link Subscriber}, {@link Processor}, and {@link Initializable} as
 * auto inject types for the {@link Injector}.
 * <li>Register an {@link EntityManager}.
 * </ul>
 * <p>
 * Typically, systems, managers, and singletons are registered via
 * {@link #with(Object)} or {@link #with(String, Object)}. Components,
 * {@link Archetype}s and {@link Transmutation}s have their dedicated
 * convenience methods.
 * <p>
 * Once all elements are registered, {@link #build()} is called to create the
 * {@link Engine}. One instance of {@link EngineBuilder} can only create one
 * {@link Engine}.
 * <p>
 * {@link #configurator(Class, Configurator)} allows to extend the engine build
 * logic.
 * 
 * @author Joannick Gardize
 *
 */
public class EngineBuilder {

	public static final int DEFAULT_EXPECTED_ENTITY_COUNT = 128;

	private int expectedEntityCount;
	private List<TypeConfigurator<?>> typeConfigurators;
	private RegistrationMap registrations;
	private Injector injector;
	private List<Processor> processors;
	private boolean defaultComponentAutoCreation;
	private boolean configuring;

	/**
	 * Creates an empty EngineBuilder with no default configuration, and an expected
	 * maximum number of entity of {@link #DEFAULT_EXPECTED_ENTITY_COUNT}.
	 */
	public EngineBuilder() {
		this(DEFAULT_EXPECTED_ENTITY_COUNT);
	}

	/**
	 * Creates an empty EngineBuilder with no default configuration.
	 * 
	 * @param expectedEntityCount the expected maximum number of entity.
	 */
	public EngineBuilder(int expectedEntityCount) {
		if (expectedEntityCount < 1) {
			throw new EngineConfigurationException("expectedEntityCount must be greater than zero");
		}
		this.expectedEntityCount = expectedEntityCount;
		typeConfigurators = new ArrayList<>();
		registrations = new RegistrationMap();
		injector = new Injector();
		processors = new ArrayList<>();
		defaultComponentAutoCreation = true;
		configuring = true;
	}

	/**
	 * Creates an EngineBuilder with the minimal default configuration as mentioned
	 * above, and an expected maximum number of entity of
	 * {@link #DEFAULT_EXPECTED_ENTITY_COUNT}.
	 * 
	 * @return an EngineBuilder with default configuration
	 */
	public static EngineBuilder withDefaults() {
		return withDefaults(DEFAULT_EXPECTED_ENTITY_COUNT);
	}

	/**
	 * Creates an EngineBuilder with the minimal default configuration as mentioned
	 * above.
	 * 
	 * @param expectedEntityCount the expected maximum number of entity.
	 * @return an EngineBuilder with default configuration
	 */
	public static EngineBuilder withDefaults(int expectedEntityCount) {
		EngineBuilder builder = new EngineBuilder(expectedEntityCount);

		builder.configurator(Subscriber.class, DefaultConfigurators::configureSubscriber);
		builder.configurator(Archetype.class, DefaultConfigurators::configureArchetype);
		builder.configurator(Transmutation.class, new DefaultConfigurators.TransmutationConfigurator());
		builder.configurator(Processor.class, DefaultConfigurators::configureProcessors);
		builder.configurator(Object.class, builder.getInjector());
		builder.configurator(Initializable.class, DefaultConfigurators::configureInitializables);

		builder.with(new EntityManager(expectedEntityCount));

		builder.autoInjectType(Processor.class);
		builder.autoInjectType(Subscriber.class);
		builder.autoInjectType(Initializable.class);

		return builder;
	}

	/**
	 * Add the given configurator to this build. It will be called during the final
	 * build after all previously added configurators, for each registration with a
	 * type assignable from the given {@code type}.
	 * 
	 * @param <T>          the type concerned by the configurator
	 * @param type         the type the configurator is interested to process
	 * @param configurator the configurator to call during the final build
	 */
	public <T> void configurator(Class<T> type, Configurator<T> configurator) {
		checkConfiguring();
		typeConfigurators.add(new TypeConfigurator<>(type, configurator));
	}

	public <T> void configuratorBefore(Class<T> type, Configurator<T> configurator, Class<?> before) {
		checkConfiguring();
		typeConfigurators.add(indexOfConfigurator(before), new TypeConfigurator<>(type, configurator));
	}

	public <T> void configuratorAfter(Class<T> type, Configurator<T> configurator, Class<?> after) {
		checkConfiguring();
		typeConfigurators.add(indexOfConfigurator(after) + 1, new TypeConfigurator<>(type, configurator));
	}

	public <T> void configuratorBeforeInjection(Class<T> type, Configurator<T> configurator) {
		configuratorBefore(type, configurator, Object.class);
	}

	private int indexOfConfigurator(Class<?> type) {
		for (int i = 0; i < typeConfigurators.size(); i++) {
			if (typeConfigurators.get(i).getType() == type) {
				return i;
			}
		}
		throw new EngineConfigurationException("No configurator for type " + type);
	}

	/**
	 * Register a {@link ComponentMapper} for the given component type.
	 * {@link FlatArrayComponentMapper} is used.
	 * 
	 * @param <T>                 the component type
	 * @param type                the component type
	 * @param newInstanceSupplier a constructor reference for the given {@code type}
	 */
	public <T> void component(Class<T> type, Supplier<T> newInstanceSupplier) {
		component(type, new FlatArrayComponentMapper<>(expectedEntityCount, newInstanceSupplier));
	}

	/**
	 * Register the given {@link ComponentMapper} for the given component
	 * {@code type}.
	 * 
	 * @param <T>             the component type
	 * @param type            the component type
	 * @param componentMapper The {@link ComponentMapper} to register for the given
	 *                        component {@code type}
	 */
	public <T> void component(Class<T> type, ComponentMapper<T> componentMapper) {
		checkConfiguring();
		registrations.put(ComponentMapper.class, type, componentMapper);
	}

	/**
	 * Register a new entity {@link Archetype}.
	 * 
	 * @param name           the name of the archetype for field injection
	 * @param componentTypes the component types the archetype is made of
	 * @return the newly created Archetype
	 */
	public Archetype archetype(String name, Class<?>... componentTypes) {
		checkConfiguring();
		Archetype archetype = new Archetype(name, registrations.typeCount(Archetype.class), componentTypes);
		with(name, archetype);
		return archetype;
	}

	/**
	 * Register a new entity {@link Transmutation}.
	 * 
	 * @param from the Archetype the transmutation starts from
	 * @param to   the resulting Archetype of the transmutation
	 */
	public void transmutation(Archetype from, Archetype to) {
		checkConfiguring();
		Transmutation transmutation = new Transmutation(from, to);
		registrations.put(transmutation, transmutation);
	}

	/**
	 * Register a new entity {@link Transmutation}. Uses the archetype name as it
	 * was declared in {@link #archetype(String, Class...)}.
	 * 
	 * @param from the Archetype name the transmutation starts from
	 * @param to   the resulting Archetype name of the transmutation
	 */
	public void transmutation(String from, String to) {
		checkConfiguring();
		Archetype fromArchetype = registrations.getOrFail(Archetype.class, from);
		Archetype toArchetype = registrations.getOrFail(Archetype.class, to);
		transmutation(fromArchetype, toArchetype);
	}

	/**
	 * Register the given object. Injection will be done by field type.
	 * 
	 * @param object the object to register
	 */
	public void with(Object object) {
		checkConfiguring();
		registrations.put(object);
	}

	/**
	 * Register the given object. Injection will be done by field type and name.
	 * 
	 * @param name   the name of the registration for field name matching during
	 *               injection
	 * @param object the object to register
	 */
	public void with(String name, Object object) {
		checkConfiguring();
		registrations.put(name, object);
	}

	/**
	 * Convenience method for {@link Injector#addAutoInjectType(Class)}. The given
	 * class will be automatically injected without the need of marking it with
	 * {@link Injector}.
	 * 
	 * @param type
	 */
	public void autoInjectType(Class<?> type) {
		injector.addAutoInjectType(type);
	}

	/**
	 * <p>
	 * Set the default auto creation setting of components.
	 * <p>
	 * If true, components are automatically created via
	 * {@link ComponentMapper#create(int)} at entity creation and transmutation.
	 * <p>
	 * If false, components must be inserted manually via
	 * {@link ComponentMapper#put(int, Object)}. This is useful to avoid a get
	 * operation for components that systematically requires initialization at
	 * creation or transmutation.
	 * <p>
	 * True by default.
	 * 
	 * @param defaultComponentAutoCreation
	 */
	public void defaultComponentAutoCreation(boolean defaultComponentAutoCreation) {
		this.defaultComponentAutoCreation = defaultComponentAutoCreation;
	}

	/**
	 * @return the default auto creation setting of components
	 */
	public boolean defaultComponentAutoCreation() {
		return defaultComponentAutoCreation;
	}

	/**
	 * <p>
	 * Add a {@link Processor} for the resulting {@link Engine}.
	 * <p>
	 * This is a building operation that can only be called during the build (with
	 * {@link #build()}). This method should not be called unless for a custom
	 * implementation of a Processor Configurator.
	 * 
	 * @param processor the processor to add to the {@link Engine}
	 */
	public void addProcessor(Processor processor) {
		checkNotConfiguring();
		processors.add(processor);
	}

	/**
	 * Build the engine, calling successively all the {@link Configurator}s, and
	 * then injects all fields of all registered objects using the {@link Injector}.
	 * 
	 * @return the ready-to-use Engine
	 */
	public Engine build() {
		checkConfiguring();
		configuring = false;
		applyConfigurators();
		return new Engine(processors.toArray(new Processor[processors.size()]));
	}

	/**
	 * @return the expected maximum number of entity
	 */
	public int getExpectedEntityCount() {
		return expectedEntityCount;
	}

	/**
	 * @return the registration map containing all registered objects
	 */
	public RegistrationMap getRegistrations() {
		return registrations;
	}

	/**
	 * @return the injector used for field injection
	 */
	public Injector getInjector() {
		return injector;
	}

	private void applyConfigurators() {
		for (TypeConfigurator<?> configurator : typeConfigurators) {
			registrations.forEach(o -> configurator.configure(o, this));
		}
	}

	private void checkConfiguring() {
		if (!configuring) {
			throw new EngineConfigurationException("Cannot configure or re-call build() once build began");
		}
	}

	private void checkNotConfiguring() {
		if (configuring) {
			throw new EngineConfigurationException("Configuration must be done");
		}
	}
}