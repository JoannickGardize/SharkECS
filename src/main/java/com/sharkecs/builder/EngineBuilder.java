/*
 * Copyright 2024 Joannick Gardize
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.sharkecs.builder;

import com.sharkecs.*;
import com.sharkecs.Archetype.ComponentCreationPolicy;
import com.sharkecs.annotation.Inject;
import com.sharkecs.builder.configurator.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;

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
        EntityManager entityManager = new EntityManager(expectedEntityCount);

        return new EngineBuilder(expectedEntityCount)
                .with(new Prioritizer())
                // Configurators
                .with(new SubscriberConfigurator())
                .then(new ArchetypeConfigurator())
                .then(new TransmutationConfigurator())
                .then(new ProcessorConfigurator())
                .then(new Injector())
                .then(new InitializableConfigurator())
                // Entity Manager
                .with(entityManager)
                .before(entityManager, Processor.class)
                // Auto-inject types
                .autoInjectType(Processor.class)
                .autoInjectType(Subscriber.class)
                .autoInjectType(Initializable.class);
    }

    /**
     * Register the given object. Injection will be done by field type.
     *
     * @param object the object to register
     * @return this for chaining
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public EngineBuilder with(Object object) {
        with((Class) object.getClass(), null, object);
        return this;
    }

    /**
     * Register the given object. Injection will be done by field type and name.
     *
     * @param name   the name of the registration for field name matching during
     *               injection. Giving a null value is equivalent to
     *               {@link #with(Object)}
     * @param object the object to register
     * @return this for chaining
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public EngineBuilder with(String name, Object object) {
        with((Class) object.getClass(), name, object);
        return this;
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
     * @return this for chaining
     */
    public <T> EngineBuilder with(Class<? super T> registrationType, T object) {
        with(registrationType, null, object);
        return this;
    }

    /**
     * Register the given object. Injection will be done by field type and generic
     * type parameter of the class. Does not support multiple generic type parameters,
     * only the first type parameter is considered.
     * <p>
     * This method should not be confused with {@link #with(Class, Object)}.
     *
     * @param genericType the generic type as registration key
     * @param object      the object to register
     * @return this for chaining
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public EngineBuilder withGeneric(Class<?> genericType, Object object) {
        with((Class) object.getClass(), genericType, object);
        return this;
    }

    /**
     * Register the given object. Injection will be done by field type. Adds a
     * priority rule to be after the previously registered object.
     *
     * @param object the object to register with a priority after the previously
     *               registered object
     * @return this for chaining
     */
    public EngineBuilder then(Object object) {
        then(null, object);
        return this;
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
     * @return this for chaining
     */
    public EngineBuilder then(String name, Object object) {
        if (previousObject == null) {
            throw new EngineConfigurationException("No previous object to prioritize");
        }
        before(previousObject, object);
        with(name, object);
        return this;
    }

    /**
     * Register a {@link ComponentMapper} for the given component type.
     * {@link FlatArrayComponentMapper} is used.
     *
     * @param <T>                 the component type
     * @param type                the component type
     * @param newInstanceSupplier a constructor reference for the given {@code type}
     * @return this for chaining
     */
    public <T> EngineBuilder component(Class<T> type, Supplier<T> newInstanceSupplier) {
        component(type, new FlatArrayComponentMapper<>(expectedEntityCount, newInstanceSupplier));
        return this;
    }

    /**
     * Register the given {@link ComponentMapper} for the given component
     * {@code type}.
     *
     * @param <T>             the component type
     * @param type            the component type
     * @param componentMapper The {@link ComponentMapper} to register for the given
     *                        component {@code type}
     * @return this for chaining
     */
    public <T> EngineBuilder component(Class<T> type, ComponentMapper<T> componentMapper) {
        checkConfiguring();
        registrations.put(ComponentMapper.class, type, componentMapper);
        return this;
    }

    /**
     * Register a new entity {@link Archetype}.
     *
     * @param name           the name of the archetype for field injection
     * @param componentTypes the component types the archetype is made of
     * @return this for chaining
     */
    public EngineBuilder archetype(String name, Class<?>... componentTypes) {
        checkConfiguring();
        with(name, new Archetype(name, componentTypes));
        return this;
    }

    /**
     * Calls {@link Archetype#setComponentCreationPolicy(ComponentCreationPolicy, Class[])}
     * from the previously added archetype.
     *
     * @param componentCreationPolicy see {@link Archetype#setComponentCreationPolicy(ComponentCreationPolicy, Class[])}
     * @param componentTypes          see {@link Archetype#setComponentCreationPolicy(ComponentCreationPolicy, Class[])}
     * @return this for chaining
     */
    public EngineBuilder componentCreationPolicy(ComponentCreationPolicy componentCreationPolicy,
                                                 Class<?>... componentTypes) {
        if (!(previousObject instanceof Archetype)) {
            throw new EngineConfigurationException("The actual object is not an archetype");
        }
        ((Archetype) previousObject).setComponentCreationPolicy(componentCreationPolicy, componentTypes);
        return this;
    }

    /**
     * Calls {@link Archetype#setComponentCreationPolicy(ComponentCreationPolicy)}
     * from the previously added archetype.
     *
     * @param componentCreationPolicy see {@link Archetype#setComponentCreationPolicy(ComponentCreationPolicy)}
     * @return this for chaining
     */
    public EngineBuilder componentCreationPolicy(ComponentCreationPolicy componentCreationPolicy) {
        if (!(previousObject instanceof Archetype)) {
            throw new EngineConfigurationException("The actual object is not an archetype");
        }
        ((Archetype) previousObject).setComponentCreationPolicy(componentCreationPolicy);
        return this;
    }

    /**
     * Register a new entity {@link Transmutation}.
     *
     * @param from the Archetype the transmutation starts from
     * @param to   the resulting Archetype of the transmutation
     * @return this for chaining
     */
    public EngineBuilder transmutation(Archetype from, Archetype to) {
        checkConfiguring();
        Transmutation transmutation = new Transmutation(from, to);
        registrations.put(transmutation, transmutation);
        return this;
    }

    /**
     * Register a new entity {@link Transmutation}. Uses the archetype name as it
     * was declared in {@link #archetype(String, Class...)}.
     *
     * @param from the Archetype name the transmutation starts from
     * @param to   the resulting Archetype name of the transmutation
     * @return this for chaining
     */
    public EngineBuilder transmutation(String from, String to) {
        checkConfiguring();
        Archetype fromArchetype = registrations.getOrFail(Archetype.class, from);
        Archetype toArchetype = registrations.getOrFail(Archetype.class, to);
        transmutation(fromArchetype, toArchetype);
        return this;
    }

    /**
     * Calls {@link Prioritizer#before(Object, Object...)}. Adds a priority rule to
     * {@code before} to be before each {@code after}. Parameters can be
     * registration instances, "marker" instances, registration types or supertypes,
     * or annotation types.
     *
     * @param before the object / type / annotation to be before each {@code after}
     *               elements
     * @param after  the objects / types / annotations to be after the
     *               {@code before} element
     * @return this for chaining
     */
    public EngineBuilder before(Object before, Object... after) {
        registrations.getOrFail(Prioritizer.class).before(before, after);
        return this;
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
     * @return this for chaining
     */
    public EngineBuilder after(Object after, Object... before) {
        registrations.getOrFail(Prioritizer.class).after(after, before);
        return this;
    }

    /**
     * Convenience method that calls {@link #before(Object, Object...)} successively
     * for each pair of object in parameter.
     *
     * @param chain the chain of object to prioritize
     * @return this for chaining
     */
    public EngineBuilder priorityChain(Object... chain) {
        int end = chain.length - 1;
        for (int i = 0; i < end; i++) {
            before(chain[i], chain[i + 1]);
        }
        return this;
    }

    /**
     * Convenience method to change the default component creation policy of the
     * {@link ArchetypeConfigurator}. The default value is
     * {@link ComponentCreationPolicy#MANUAL}, so the user intended to manually add
     * the components of a created or mutated entity, avoiding a
     * {@link ComponentMapper} access when the component requires some
     * initializations.
     *
     * @param defaultComponentCreationPolicy the new default component creation
     *                                       policy
     * @return this for chaining
     * @throws EngineConfigurationException if there is no
     *                                      {@link ArchetypeConfigurator} registered
     *                                      in this EngineBuilder
     */
    public EngineBuilder defaultComponentCreationPolicy(ComponentCreationPolicy defaultComponentCreationPolicy) {
        registrations.getOrFail(ArchetypeConfigurator.class)
                .setDefaultComponentCreationPolicy(defaultComponentCreationPolicy);
        return this;
    }

    /**
     * Convenience method to call {@link Injector#addAutoInjectType(Class)}. The
     * given class will be automatically injected without the need of marking it
     * with {@link Inject}.
     *
     * @param type the type to mark as auto-inject type.
     * @return this for chaining
     */
    public EngineBuilder autoInjectType(Class<?> type) {
        registrations.getOrFail(Injector.class).addAutoInjectType(type);
        return this;
    }

    /**
     * Convenience method to call {@link Injector#setFailWhenNotFound(boolean)}
     *
     * @param failWhenNotFound see {@link Injector#setFailWhenNotFound(boolean)}
     * @return this for chaining
     */
    public EngineBuilder failInjectionWhenNotFound(boolean failWhenNotFound) {
        registrations.getOrFail(Injector.class).setFailWhenNotFound(failWhenNotFound);
        return this;
    }

    /**
     * Convenience method to call {@link Injector#setInjectAnyAssignableType(boolean)}
     *
     * @param injectAnyAssignableType see {@link Injector#setInjectAnyAssignableType(boolean)}
     * @return this for chaining
     */
    public EngineBuilder injectAnyAssignableType(boolean injectAnyAssignableType) {
        registrations.getOrFail(Injector.class).setInjectAnyAssignableType(injectAnyAssignableType);
        return this;
    }

    /**
     * Convenience method to register a Configurator, after the {@link Injector} and
     * before the {@link InitializableConfigurator}.
     *
     * @param configurator the configurator to register
     * @return this for chaining
     */
    public EngineBuilder configurator(Configurator configurator) {
        with(configurator);
        priorityChain(Injector.class, configurator, InitializableConfigurator.class);
        return this;
    }

    /**
     * Convenience method to register a Configurator, before the {@link Injector}.
     *
     * @param configurator the configurator to register
     * @return this for chaining
     */
    public EngineBuilder configuratorBeforeInjection(Configurator configurator) {
        with(configurator);
        priorityChain(ProcessorConfigurator.class, configurator, Injector.class);
        return this;
    }

    /**
     * Set the root configurator to use. The default is a {@link RootConfigurator},
     * which calls all registered configurators in their priority orders.
     *
     * @param rootConfigurator the root configurator to use
     * @return this for chaining
     */
    public EngineBuilder rootConfigurator(Configurator rootConfigurator) {
        this.rootConfigurator = rootConfigurator;
        return this;
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
     * then injects all fields of all registered objects using the {@link Injector} (by default).
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
     * matches the given composition
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
     * matches the given composition
     * @throws EngineConfigurationException if the configuration of the
     *                                      ArchetypeConfigurator hasn't been done
     *                                      yet
     */
    public Archetype getArchetype(Class<?>... composition) {
        return registrations.getOrFail(ArchetypeConfigurator.class).of(composition);
    }

    private void checkConfiguring() {
        if (!configuring) {
            throw new EngineConfigurationException("Cannot configure or re-call build() once the build began");
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