# SharkECS
Entity Component System implementation focused on performance and ease of use. Inspired of the well known [Artemis-odb](https://github.com/junkdog/artemis-odb) framework.

The specificity of SharkECS is that all possible entity composition and mutation must be declared first. In this way, performance is easily optimized.

## Getting started

[EngineBuilder](https://github.com/JoannickGardize/SharkECS/blob/main/src/main/java/com/sharkecs/builder/EngineBuilder.java) is used to configure the Engine, here's a simple fictitious exemple of an engine made of player(s) and bullets that could damage them:
```java
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
		builder.with(PhysicsSystem.class);
		builder.with(ImpactDamageSystem.class);

		// Register some singletons
		builder.with(Time.class);

		return builder.build();
	}
}
```
