package com.sharkecs.builder.configurator;

import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.sharkecs.Archetype;
import com.sharkecs.Archetype.ComponentCreationPolicy;
import com.sharkecs.Aspect;
import com.sharkecs.ComponentMapper;
import com.sharkecs.Subscription;
import com.sharkecs.Transmutation;
import com.sharkecs.builder.EngineBuilder;
import com.sharkecs.builder.EngineConfigurationException;
import com.sharkecs.builder.RegistrationMap;

/**
 * {@link Configurator} of {@link Archetype}s. Creates the arrays of
 * {@link Subscription}, {@link ComponentMapper} and {@link Transmutation}
 * related to the Archetype.
 * 
 * @author Joannick Gardize
 *
 */
public class ArchetypeConfigurator extends TypeConfigurator<Archetype> {

    private Map<Set<Class<?>>, Archetype> byComposition;

    private ComponentCreationPolicy defaultComponentCreationPolicy = ComponentCreationPolicy.MANUAL;

    private int nextId;

    public ArchetypeConfigurator() {
        super(Archetype.class);
    }

    @Override
    protected void beginConfiguration(EngineBuilder engineBuilder) {
        nextId = 0;
    }

    @Override
    @SuppressWarnings("unchecked")
    protected void configure(Archetype archetype, EngineBuilder engineBuilder) {
        RegistrationMap registrations = engineBuilder.getRegistrations();
        archetype.setId(nextId++);
        archetype.setSubscriptions(registrations.entrySet(Subscription.class).stream()
                .filter(e -> ((Aspect) e.getKey()).matches(archetype.getComposition())).map(Entry::getValue)
                .toArray(Subscription[]::new));
        archetype.setComponentMappers(archetype.getComposition().stream()
                .map(t -> registrations.getOrFail(ComponentMapper.class, t)).toArray(ComponentMapper[]::new));
        archetype.setAutoCreateComponentMappers(archetype.getComposition().stream()
                .filter(t -> archetype.getComponentCreationPolicy(t,
                        defaultComponentCreationPolicy) == ComponentCreationPolicy.AUTOMATIC)
                .map(t -> registrations.getOrFail(ComponentMapper.class, t)).toArray(ComponentMapper[]::new));
        archetype.setTransmutations(new Transmutation[registrations.typeCount(Archetype.class)]);
        archetype.setAdditiveTransmutations(new IdentityHashMap<>());
        archetype.setSuppressiveTransmutations(new IdentityHashMap<>());
        archetype.markConfigured();
    }

    @Override
    protected void endConfiguration(EngineBuilder engineBuilder) {
        byComposition = new HashMap<>();
        for (Archetype archetype : engineBuilder.getRegistrations().getAllAssignableFrom(Archetype.class)) {
            byComposition.put(archetype.getComposition(), archetype);
        }
    }

    /**
     * Returns the archetype made of the given composition. Configuration of this
     * configurator must be done before calling this method.
     * 
     * @param composition the component composition of the archetype to return
     * @return the archetype of the given composition, or null if no archetype
     *         matches the given composition
     * @throws EngineConfigurationException if the configuration of this
     *                                      configurator hasn't been done yet
     */
    public Archetype of(Set<Class<?>> composition) {
        if (byComposition == null) {
            throw new EngineConfigurationException("not configured yet");
        }
        return byComposition.get(composition);
    }

    /**
     * Convenience method to call {@link #of(Set)} with the given composition array.
     * 
     * @param composition the component composition of the archetype to return
     * @return the archetype of the given composition, or null if no archetype
     *         matches the given composition
     * @throws EngineConfigurationException if the configuration of this
     *                                      configurator hasn't been done yet
     */
    public Archetype of(Class<?>... composition) {
        Map<Class<?>, Class<?>> compositionMap = new IdentityHashMap<>();
        for (Class<?> type : composition) {
            compositionMap.put(type, type);
        }
        return of(compositionMap.keySet());
    }

    public ComponentCreationPolicy getDefaultComponentCreationPolicy() {
        return defaultComponentCreationPolicy;
    }

    /**
     * Set the default {@link ComponentCreationPolicy}. The default value is
     * {@link ComponentCreationPolicy#MANUAL}.
     * 
     * @param defaultComponentCreationPolicy
     */
    public void setDefaultComponentCreationPolicy(ComponentCreationPolicy defaultComponentCreationPolicy) {
        this.defaultComponentCreationPolicy = defaultComponentCreationPolicy;
    }
}
