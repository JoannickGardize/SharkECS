package com.sharkecs.example;

import com.sharkecs.Engine;
import com.sharkecs.builder.EngineBuilder;
import com.sharkecs.example.component.Bullet;
import com.sharkecs.example.component.Corpse;
import com.sharkecs.example.component.Health;
import com.sharkecs.example.component.Physics;
import com.sharkecs.example.component.Shooter;
import com.sharkecs.example.singleton.Time;
import com.sharkecs.example.singleton.Viewport;
import com.sharkecs.example.system.BulletDamageSystem;
import com.sharkecs.example.system.BulletLifetimeSystem;
import com.sharkecs.example.system.ConsoleCleaner;
import com.sharkecs.example.system.DeathSystem;
import com.sharkecs.example.system.EntityDrawingSystem;
import com.sharkecs.example.system.HeaderDrawer;
import com.sharkecs.example.system.HealthDrawerSystem;
import com.sharkecs.example.system.PhysicsSystem;
import com.sharkecs.example.system.ShootSystem;
import com.sharkecs.example.system.TimeManager;

public class ExampleUtils {

	public static Engine createEngine(boolean withGraphics) {

		EngineBuilder builder = EngineBuilder.withDefaults();

		// Register component types
		builder.component(Physics.class, Physics::new);
		builder.component(Bullet.class, Bullet::new);
		builder.component(Health.class, Health::new);
		builder.component(Shooter.class, Shooter::new);
		builder.component(Corpse.class, Corpse::new);

		// Register entity archetypes
		builder.archetype("player", Physics.class, Health.class, Shooter.class);
		builder.archetype("corpse", Physics.class, Corpse.class);
		builder.archetype("bullet", Physics.class, Bullet.class);

		// Register transmutations
		builder.transmutation("player", "corpse");

		// Register managers & systems, in the right order
		builder.with(new TimeManager());
		builder.then(new PhysicsSystem());
		builder.then(new BulletDamageSystem());
		builder.then(new BulletLifetimeSystem());
		builder.then(new ShootSystem());
		builder.then(new DeathSystem());
		if (withGraphics) {
			builder.then(new ConsoleCleaner());
			builder.then(new HeaderDrawer());
			builder.then(new HealthDrawerSystem());
			builder.then(new EntityDrawingSystem());
		}

		// Register miscellaneous stuff
		builder.with(new Time());
		builder.with(new Viewport());
		builder.with(new ExampleScenarioInitializer());

		return builder.build();
	}
}
