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

package sharkhendrix.sharkecs.builder.configurator;

import sharkhendrix.sharkecs.Archetype;
import sharkhendrix.sharkecs.Aspect;
import sharkhendrix.sharkecs.ComponentMapper;
import sharkhendrix.sharkecs.Transmutation;
import sharkhendrix.sharkecs.builder.EngineBuilder;
import sharkhendrix.sharkecs.builder.EngineConfigurationException;
import sharkhendrix.sharkecs.builder.RegistrationMap;
import sharkhendrix.sharkecs.subscription.Subscription;

import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/**
 * {@link Configurator} of {@link Archetype}s. Creates the arrays of
 * {@link Subscription}, {@link ComponentMapper} and {@link Transmutation}
 * related to the Archetype.
 */
public class ArchetypeConfigurator extends TypeConfigurator<Archetype> {

    private Map<Set<Class<?>>, Archetype> byComposition;

    private Archetype.ComponentCreationPolicy defaultComponentCreationPolicy = Archetype.ComponentCreationPolicy.MANUAL;

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
        archetype.setSubscriptions(registrations.entrySet(SubscriptionGroup.class).stream()
                .filter(e -> ((Aspect) e.getKey()).matches(archetype.getComposition())).map(Entry::getValue)
                .flatMap(g -> g.getSubscriptionsBySort().values().stream())
                .distinct()
                .toArray(Subscription[]::new));
        archetype.setComponentMappers(archetype.getComposition().stream()
                .map(t -> registrations.getOrFail(ComponentMapper.class, t)).toArray(ComponentMapper[]::new));
        archetype.setAutoCreateComponentMappers(archetype.getComposition().stream()
                .filter(t -> archetype.getComponentCreationPolicy(t,
                        defaultComponentCreationPolicy) == Archetype.ComponentCreationPolicy.AUTOMATIC)
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
     * matches the given composition
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
     * matches the given composition
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

    public Archetype.ComponentCreationPolicy getDefaultComponentCreationPolicy() {
        return defaultComponentCreationPolicy;
    }

    /**
     * Set the default {@link Archetype.ComponentCreationPolicy}. The default value is
     * {@link Archetype.ComponentCreationPolicy#MANUAL}.
     *
     * @param defaultComponentCreationPolicy
     */
    public void setDefaultComponentCreationPolicy(Archetype.ComponentCreationPolicy defaultComponentCreationPolicy) {
        this.defaultComponentCreationPolicy = defaultComponentCreationPolicy;
    }
}
