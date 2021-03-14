package com.sharkecs.builder.configurator;

import java.util.Map.Entry;

import com.sharkecs.Archetype;
import com.sharkecs.Archetype.ComponentCreationPolicy;
import com.sharkecs.Aspect;
import com.sharkecs.ComponentMapper;
import com.sharkecs.Subscription;
import com.sharkecs.Transmutation;
import com.sharkecs.builder.EngineBuilder;
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

	private ComponentCreationPolicy defaultComponentCreationPolicy = ComponentCreationPolicy.MANUAL;

	public ArchetypeConfigurator() {
		super(Archetype.class);
	}

	@Override
	@SuppressWarnings("unchecked")
	protected void configure(Archetype archetype, EngineBuilder engineBuilder) {
		RegistrationMap registrations = engineBuilder.getRegistrations();
		archetype.setSubscriptions(registrations.entrySet(Subscription.class).stream().filter(e -> ((Aspect) e.getKey()).matches(archetype.getComponentTypes()))
		        .map(Entry::getValue).toArray(Subscription[]::new));
		archetype.setComponentMappers(archetype.getComponentTypes().stream().map(t -> registrations.getOrFail(ComponentMapper.class, t)).toArray(ComponentMapper[]::new));
		archetype.setAutoCreateComponentMappers(
		        archetype.getComponentTypes().stream().filter(t -> archetype.getComponentCreationPolicy(t, defaultComponentCreationPolicy) == ComponentCreationPolicy.AUTOMATIC)
		                .map(t -> registrations.getOrFail(ComponentMapper.class, t)).toArray(ComponentMapper[]::new));
		archetype.setTransmutations(new Transmutation[registrations.typeCount(Archetype.class)]);
	}

	public ComponentCreationPolicy getDefaultComponentCreationPolicy() {
		return defaultComponentCreationPolicy;
	}

	public void setDefaultComponentCreationPolicy(ComponentCreationPolicy defaultComponentCreationPolicy) {
		this.defaultComponentCreationPolicy = defaultComponentCreationPolicy;
	}
}
