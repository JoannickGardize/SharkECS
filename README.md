# SharkECS

Entity Component System implementation focused on performance, ease of use, and flexibility, inspired by the popular [artemis-odb](https://github.com/junkdog/artemis-odb) framework.

The specificity of SharkECS regarding to other ECS frameworks is that all possible entity composition and mutation must be declared first. In this way, performance is easily optimized.

## Terminology

- **Entity**: an entity is something made of components. Entities by themselves are just an integer identifier. Entity creation, deletion, and mutation operations are made via the [EntityManager](https://github.com/JoannickGardize/SharkECS/blob/main/src/main/java/com/sharkecs/EntityManager.java). Note that entity IDs are not unique other time.
- **Component**: Components are data holders representing something some entities are made of (position, body, health, AI, sprite...). Components are accessed via [ComponentMappers](https://github.com/JoannickGardize/SharkECS/blob/main/src/main/java/com/sharkecs/ComponentMapper.java).
- **Archetype**: an [Archetype](https://github.com/JoannickGardize/SharkECS/blob/main/src/main/java/com/sharkecs/Archetype.java) is a declaration of component composition used to create entities at runtime.
- **Transmutation**: a [Transmutation](https://github.com/JoannickGardize/SharkECS/blob/main/src/main/java/com/sharkecs/Transmutation.java) is the declaration of a possible mutation operation from an Archetype to another one at runtime.
- **Aspect:** [Aspects](https://github.com/JoannickGardize/SharkECS/blob/main/src/main/java/com/sharkecs/Aspect.java) represents a group of possible entity composition (of component), Aspect declaration is made via annotation on classes implementing [Subscriber](https://github.com/JoannickGardize/SharkECS/blob/main/src/main/java/com/sharkecs/Subscriber.java).
- **Subscription**: a maintained collection of entity, generally of a given aspect. A subscription can be listened to get notified of insertion, removal, and mutation.
- **Subscriber**: a class interested to subscribe to a subscription (generally of a given aspect, via its class annotation declaration). [SubscriberAdapter](https://github.com/JoannickGardize/SharkECS/blob/main/src/main/java/com/sharkecs/SubscriberAdapter.java) is a convenient base for this kind of class. Typical systems will subscribe to a subscription and iterate over its entities during processing.
- **Processor**: a [Processor](https://github.com/JoannickGardize/SharkECS/blob/main/src/main/java/com/sharkecs/Processor.java) is something that will be processed at each engine process call.
- **System**: systems are processors of a given entity aspect, they implement a part of the game logic related to this entity aspect. In SharkECS, there is no explicit system base class to extends. Systems generally extends [SubscriberAdapter](https://github.com/JoannickGardize/SharkECS/blob/main/src/main/java/com/sharkecs/SubscriberAdapter.java) and implements [Processor](https://github.com/JoannickGardize/SharkECS/blob/main/src/main/java/com/sharkecs/SubscriberAdapter.java). [IteratingSystem](https://github.com/JoannickGardize/SharkECS/blob/main/src/main/java/com/sharkecs/IteratingSystem.java) is provided for convenience, and is the most common base class to use.
- **Manager**: manager is a general term of something providing shared behaviors / access to systems. The [EntityManager](https://github.com/JoannickGardize/SharkECS/blob/main/src/main/java/com/sharkecs/EntityManager.java) is the most relevant example.
- **Engine**: the root class, call `Engine#process()` in your main game loop. It is simply made of an array of Processor, since everything is wired during the engine building.

## Getting started

Let's take an example of an engine made of player(s) and bullets that could damage them.

### System example

Most systems extends [IteratingSystem](https://github.com/JoannickGardize/SharkECS/blob/main/src/main/java/com/sharkecs/IteratingSystem.java), which iterates over all subscribed entity each process cycle. Let's assume that we have a `PhysicsSystem`, updating a `Physics` component and its list of actually colliding other `Physics`. The system responsible of damaging players with the bullets could looks like this:

```java
@WithAll({ Physics.class, Bullet.class })
public class BulletDamageSystem extends IteratingSystem {

	private ComponentMapper<Physics> physicsMapper;
	private ComponentMapper<Bullet> bulletMapper;
	private ComponentMapper<Health> healthMapper;

	@Override
	public void process(int entityId) {
		Physics physics = physicsMapper.get(entityId);
		for (Physics colliding : physics.getCollisionGroup()) {
			int collidingId = colliding.getEntityId();
			healthMapper.ifExists(collidingId, health -> {
				Bullet bullet = bulletMapper.get(entityId);
				health.takeDamage(bullet.getDamage());
				entityManager.remove(entityId);
			});
		}
	}

	public void setPhysicsMapper(ComponentMapper<Physics> physicsMapper) {
		this.physicsMapper = physicsMapper;
	}

	public void setBulletMapper(ComponentMapper<Bullet> bulletMapper) {
		this.bulletMapper = bulletMapper;
	}

	public void setHealthMapper(ComponentMapper<Health> healthMapper) {
		this.healthMapper = healthMapper;
	}
}
```

According to the @WithAll annotation, this system will process all entities with at least a Physics and a Bullet component. The system will apply the bullet's damage once it hit a damageable (with a Health component) entity, and then remove the bullet entity.

The attributes of the system will be automatically injected during the engine building. Note the presence of setter methods: they are required and used for injection, because SharkECS is respectful of the encapsulation principle. If you feel uncomfortable with this boilerplate code, Take a look at the [Project Lombok](https://projectlombok.org/).

You can find the full running example [here](https://github.com/JoannickGardize/SharkECS/tree/main/src/test/java/com/sharkecs/example).

### EngineBuilder

[EngineBuilder](https://github.com/JoannickGardize/SharkECS/blob/main/src/main/java/com/sharkecs/builder/EngineBuilder.java) is used to configure and create the Engine:

```java
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

// Register miscellaneous stuff
builder.with(new Time());
builder.with(new Viewport());
builder.with(new ExampleScenarioInitializer());

Engine engine = builder.build();
```

Finally, call `engine.process()` in your main game loop.

The full and runnable code of this example can be found [here](https://github.com/JoannickGardize/SharkECS/tree/main/src/test/java/com/sharkecs/example)

## Entity references

`EntityManager#reference(int)` allows to creates safe references of entities, properly cleared when the referenced entity is removed.

## Priority management

You may want a specific execution order of your systems (or any sequentially executed elements: Processor, Initializable, Configurator...).

To achieve this, the naive way is to specify a priority number to elements, but this can lead to readability and maintainability issues.

In SharkECS, you specify priorities in the form of before / after constraints. There is different ways to do this:

- `EngineBuilder#then(Object)` is a convenience method to register an element **and** add an "after" priority constraint between it and the previously registered element
- `EngineBuilder#after(Object, Object...)` and `EngineBuilder#before(Object, Object...)` add constraints between registered elements, parameters could be:
  - the instance of the concerned element
  - a Class, every elements assignable to this Class will be concerned
  - an annotation type, every elements declaring this annotation will be concerned
  - any non-registered instance as a "marker" in the priority graph

For instance, the EngineBuilder's default configuration calls `builder.before(entityManager, Processor.class);` to put the EntityManager before any other Processor.

## Transmutation, injection by generic types, custom engine configurator...

This readme is not complete, to go deeper, see the javadoc of the code.
