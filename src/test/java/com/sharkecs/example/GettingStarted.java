package com.sharkecs.example;

import com.sharkecs.Engine;
import com.sharkecs.builder.EngineBuilder;

public class GettingStarted {

	public static Engine createExampleEngine() {

		EngineBuilder builder = new EngineBuilder();

		// Register component types
		builder.component(Physics.class, Physics::new);
		builder.component(Bullet.class, Bullet::new);
		builder.component(Health.class, Health::new);

		// Register entity archetypes
		builder.archetype("playerArchetype", Physics.class, Health.class);
		builder.archetype("bulletArchetype", Physics.class, Bullet.class);

		// Register systems
		builder.with(new PhysicsSystem());
		builder.then(new BulletDamageSystem());
		builder.then(new HealthSystem());

		// Register some singletons
		builder.with(new Time());
		builder.with(new Time());

		return builder.build();
	}
}
