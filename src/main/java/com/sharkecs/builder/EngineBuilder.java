package com.sharkecs.builder;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;

import com.sharkecs.Archetype;
import com.sharkecs.Archetype.ComponentCreationPolicy;
import com.sharkecs.ComponentMapper;
import com.sharkecs.Engine;
import com.sharkecs.EntityManager;
import com.sharkecs.FlatArrayComponentMapper;
import com.sharkecs.Initializable;
import com.sharkecs.Processor;
import com.sharkecs.Subscriber;
import com.sharkecs.Transmutation;
import com.sharkecs.annotation.Inject;
import com.sharkecs.builder.configurator.ArchetypeConfigurator;
import com.sharkecs.builder.configurator.Configurator;
import com.sharkecs.builder.configurator.InitializableConfigurator;
import com.sharkecs.builder.configurator.Injector;
import com.sharkecs.builder.configurator.Prioritizer;
import com.sharkecs.builder.configurator.ProcessorConfigurator;
import com.sharkecs.builder.configurator.RootConfigurator;
import com.sharkecs.builder.configurator.SubscriberConfigurator;
import com.sharkecs.builder.configurator.TransmutationConfigurator;

/**
 * <p>
 * Builder of {@link Engine}. Uses {@link #withDefaults()} to create a builder
 * with the following minimal default configuration:
 * <ul>
 * <li>Register the given {@link Configurator}s, in this priority order:
 * <ul>
 * <li>{@link Prioritizer}
 * <li>{@link SubscriberConfigurator}
 * <li>{@link ArchetypeConfigurator}
 * <li>{@link TransmutationConfigurator}
 * <li>{@link ProcessorConfigurator}
 * <li>{@link Injector}
 * <li>{@link InitializableConfigurator}
 * </ul>
 * <li>Register an {@link EntityManager}.
 * <li>Adds a priority for the {@link EntityManager} to be before all
 * {@link Processor}s.
 * <li>Adds {@link Subscriber}, {@link Processor}, and {@link Initializable} as
 * auto inject types for the {@link Injector}.
 * </ul>
 * <p>
 * Typically, systems, managers, and singletons are registered via
 * {@link #with(Object)}. Multiple instances of the same type is allowed but a
 * different name must be given via {@link #with(String, Object)} (or any other
 * key type, see overloads of {@code with(...)} and {@link RegistrationMap}).
 * Components, {@link Archetype}s and {@link Transmutation}s have their
 * dedicated convenience methods.
 * <p>
 * Once all elements are registered, {@link #build()} is called to create the
 * {@link Engine}. One instance of {@link EngineBuilder} can only create one
 * {@link Engine}.
 * <p>
 * {@link RootConfigurator} is used by default as the root configurator, it will
 * run all {@link Configurator}s in their priority order. registering a custom
 * {@link Configurator} allows to extend the build logic.
 * 
 * @author Joannick Gardize
 *
 */
public class EngineBuilder {

    public static final int DEFAULT_EXPECTED_ENTITY_COUNT = 128;

    private int expectedEntityCount;
    private Configurator rootConfigurator;
    private RegistrationMap registrations;
    private List<Processor> processors;

    private boolean configuring;
    private Object previousObject;

    /**
     * Creates an empty EngineBuilder with no default configuration, an expected
     * maximum number of entity of {@link #DEFAULT_EXPECTED_ENTITY_COUNT}, and a
     * {@link RootConfigurator} as root configurator.
     */
    public EngineBuilder() {
        this(DEFAULT_EXPECTED_ENTITY_COUNT);
    }

    /**
     * Creates an empty EngineBuilder with no default configuration, and a
     * {@link RootConfigurator} as root configurator.
     * 
     * @param expectedEntityCount the expected maximum number of entity.
     */
    public EngineBuilder(int expectedEntityCount) {
        this(expectedEntityCount, new RootConfigurator());
    }

    /**
     * Creates an empty EngineBuilder with no default configuration.
     * 
     * @param expectedEntityCount the expected maximum number of entity.
     * @param rootConfigurator    the root configurator to use
     */
    public EngineBuilder(int expectedEntityCount, Configurator rootConfigurator) {
        if (expectedEntityCount < 1) {
            throw new EngineConfigurationException("expectedEntityCount must be greater than zero");
        }
        this.expectedEntityCount = expectedEntityCount;
        this.rootConfigurator = rootConfigurator;
        registrations = new RegistrationMap();
        processors = new ArrayList<>();
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

        builder.with(new Prioritizer());

        builder.with(new SubscriberConfigurator());
        builder.then(new ArchetypeConfigurator());
        builder.then(new TransmutationConfigurator());
        builder.then(new ProcessorConfigurator());
        builder.then(new Injector());
        builder.then(new InitializableConfigurator());

        EntityManager entityManager = new EntityManager(expectedEntityCount);
        builder.with(entityManager);
        builder.before(entityManager, Processor.class);

        builder.autoInjectType(Processor.class);
        builder.autoInjectType(Subscriber.class);
        builder.autoInjectType(Initializable.class);

        return builder;
    }

    /**
     * Register the given object. Injection will be done by field type.
     * 
     * @param object the object to register
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public void with(Object object) {
        with((Class) object.getClass(), null, object);
    }

    /**
     * Register the given object. Injection will be done by field type and name.
     * 
     * @param name   the name of the registration for field name matching during
     *               injection. Giving a null value is equivalent to
     *               {@link #with(Object)}
     * @param object the object to register
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public void with(String name, Object object) {
        with((Class) object.getClass(), name, object);
    }

    /**
     * <p>
     * Register the given object. Injection will be done by field of type
     * {@code registrationType}.
     * <p>
     * This method should not be confused with {@link #withGeneric(Class, Object)}.
     * 
     * @param <T>              the registered object type
     * @param registrationType the registration type, used for field type matching
     *                         at injection
     * @param object           the object to register
     */
    public <T> void with(Class<? super T> registrationType, T object) {
        with(registrationType, null, object);
    }

    /**
     * Register the given object. Injection will be done by field type and generic
     * type parameter. Does not supports multiple generic type parameters, only the
     * first raw type parameter is considered.
     * <p>
     * This method should not be confused with {@link #with(Class, Object)}.
     * 
     * @param genericType
     * @param object
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public void withGeneric(Class<?> genericType, Object object) {
        with((Class) object.getClass(), genericType, object);
    }

    /**
     * Register the given object. Injection will be done by field type. Adds a
     * priority rule to be after the previously registered object.
     * 
     * @param object the object to register with a priority after the previously
     *               registered object
     */
    public void then(Object object) {
        then(null, object);
    }

    /**
     * Register the given object. Injection will be done by field type and name.
     * Adds a priority rule to be after the previously registered object.
     * 
     * @param name   the name of the registration for field name matching during
     *               injection. Giving a null value is equivalent to
     *               {@link #then(Object)}
     * @param object the object to register with a priority after the previously
     *               registered object
     */
    public void then(String name, Object object) {
        if (previousObject == null) {
            throw new EngineConfigurationException("No previous object to prioritize");
        }
        before(previousObject, object);
        with(name, object);
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
        Archetype archetype = new Archetype(name, componentTypes);
        with(name, archetype);
        return archetype;
    }

    /**
     * Register a new entity {@link Transmutation}.
     * 
     * @param from the Archetype the transmutation starts from
     * @param to   the resulting Archetype of the transmutation
     * @return the newly created transmutation
     */
    public Transmutation transmutation(Archetype from, Archetype to) {
        checkConfiguring();
        Transmutation transmutation = new Transmutation(from, to);
        registrations.put(transmutation, transmutation);
        return transmutation;
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
     * Calls {@link Prioritizer#before(Object, Object...)}. Adds a priority rule to
     * {@code before} to be before each {@code after}. Parameters can be
     * registration instances, "marker" instances, registration types, or annotation
     * types.
     * 
     * @param before the object / type / annotation to be before each {@code after}
     *               elements
     * @param after  the objects / types / annotations to be after the
     *               {@code before} element
     */
    public void before(Object before, Object... after) {
        registrations.getOrFail(Prioritizer.class).before(before, after);
    }

    /**
     * Calls {@link Prioritizer#after(Object, Object...)}. Adds a priority rule to
     * {@code after} to be after each {@code before}. Parameters can be registration
     * instances, "marker" instances, registration types, or annotation types.
     * 
     * @param after  the object / type / annotation to be after each {@code before}
     *               elements
     * @param before the objects / types / annotations to be before the
     *               {@code after} element
     */
    public void after(Object after, Object... before) {
        registrations.getOrFail(Prioritizer.class).after(after, before);
    }

    /**
     * Convenience method that calls {@link #before(Object, Object...)} successively
     * for each pair of object in parameter.
     * 
     * @param chain the chain of object to prioritize
     */
    public void priorityChain(Object... chain) {
        int end = chain.length - 1;
        for (int i = 0; i < end; i++) {
            before(chain[i], chain[i + 1]);
        }
    }

    /**
     * Convenience method to change the default component creation policy of the
     * {@link ArchetypeConfigurator}. The default value
     * {@link ComponentCreationPolicy#MANUAL}, so the user intended to manually add
     * the components of a created or mutated entity, avoiding a
     * {@link ComponentMapper} access when the component requires some
     * initializations.
     * 
     * @param defaultComponentCreationPolicy the new default component creation
     *                                       policy
     * @throws EngineConfigurationException if there is no
     *                                      {@link ArchetypeConfigurator} registered
     *                                      in this EngineBuilder
     */
    public void defaultComponentCreationPolicy(ComponentCreationPolicy defaultComponentCreationPolicy) {
        registrations.getOrFail(ArchetypeConfigurator.class)
                .setDefaultComponentCreationPolicy(defaultComponentCreationPolicy);
    }

    /**
     * Convenience method to call {@link Injector#addAutoInjectType(Class)}. The
     * given class will be automatically injected without the need of marking it
     * with {@link Inject}.
     * 
     * @param type
     */
    public void autoInjectType(Class<?> type) {
        registrations.getOrFail(Injector.class).addAutoInjectType(type);
    }

    /**
     * Convenience method to call {@link Injector#setFailWhenNotFound(boolean)}
     * 
     * @param failWhenNotFound
     */
    public void failInjectionWhenNotFound(boolean failWhenNotFound) {
        registrations.getOrFail(Injector.class).setFailWhenNotFound(failWhenNotFound);
    }

    /**
     * Convenience method to call
     * {@link Injector#setInjectAnyAssignableType(boolean)}
     * 
     * @param injectAnyAssignableType
     */
    public void injectAnyAssignableType(boolean injectAnyAssignableType) {
        registrations.getOrFail(Injector.class).setInjectAnyAssignableType(injectAnyAssignableType);
    }

    /**
     * Convenience method to register a Configurator, after the {@link Injector} and
     * before the {@link InitializableConfigurator}.
     * 
     * @param configurator the configurator to register
     */
    public void configurator(Configurator configurator) {
        with(configurator);
        priorityChain(Injector.class, configurator, InitializableConfigurator.class);
    }

    /**
     * Convenience method to register a Configurator, before the {@link Injector}.
     * 
     * @param configurator the configurator to register
     */
    public void configuratorBeforeInjection(Configurator configurator) {
        with(configurator);
        priorityChain(ProcessorConfigurator.class, configurator, Injector.class);
    }

    /**
     * Set the root configurator to use. The default is a {@link RootConfigurator},
     * which calls all registered configurators in their priority orders.
     * 
     * @param rootConfigurator the root configurator to use
     */
    public void setRootConfigurator(Configurator rootConfigurator) {
        this.rootConfigurator = rootConfigurator;
    }

    /**
     * <p>
     * Add a {@link Processor} for the resulting {@link Engine}.
     * <p>
     * This is a building operation that can only be called during the build (with
     * {@link #build()}). This method should not be called unless for a custom
     * implementation of a Processor {@link Configurator}.
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
        rootConfigurator.configure(this);
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
     * Convenience method to call {@link ArchetypeConfigurator#of(Set)}. the
     * ArchetypeConfigurator must be configured before calling this method.
     * 
     * @param composition the component composition of the archetype to return
     * @return the archetype of the given composition, or null if no archetype
     *         matches the given composition
     * @throws EngineConfigurationException if the configuration of the
     *                                      ArchetypeConfigurator hasn't been done
     *                                      yet
     */
    public Archetype getArchetype(Set<Class<?>> composition) {
        return registrations.getOrFail(ArchetypeConfigurator.class).of(composition);
    }

    /**
     * Convenience method to call {@link ArchetypeConfigurator#of(Class...)}. the
     * ArchetypeConfigurator must be configured before calling this method.
     * 
     * @param composition the component composition of the archetype to return
     * @return the archetype of the given composition, or null if no archetype
     *         matches the given composition
     * @throws EngineConfigurationException if the configuration of the
     *                                      ArchetypeConfigurator hasn't been done
     *                                      yet
     */
    public Archetype getArchetype(Class<?>... composition) {
        return registrations.getOrFail(ArchetypeConfigurator.class).of(composition);
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

    /**
     * Private to avoid ambiguity, {@link #getRegistrations()} should be used to get
     * full access of registration type and keys
     * 
     * @param <T>
     * @param registrationType
     * @param key
     * @param object
     */
    private <T> void with(Class<? super T> registrationType, Object key, T object) {
        checkConfiguring();
        registrations.put(registrationType, key, object);
        previousObject = object;

    }
}