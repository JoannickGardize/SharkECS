**DISCLAISMER**: Early development phase, things may drastically change over time and major bugs could exists.

# SharkECS

Entity Component System implementation focused on performance and ease of use.

The specificity of SharkECS regarding to other ECS frameworks is that all possible entity composition and mutation must be declared first. In this way, performance is easily optimized.

## Terminology

- **Entity**: entities are simple integer id, representing something made of components. Entity creation, deletion, and mutation operations are made via the [EntityManager](https://github.com/JoannickGardize/SharkECS/blob/main/src/main/java/com/sharkecs/EntityManager.java). Note that entity ids are not unique other time.
- **Component**: Components are pure data holders representing an aspect of entities (position, body, health, sprite...). Components are accessed via [ComponentMappers](https://github.com/JoannickGardize/SharkECS/blob/main/src/main/java/com/sharkecs/ComponentMapper.java).
- **Archetype**: an [Archetype](https://github.com/JoannickGardize/SharkECS/blob/main/src/main/java/com/sharkecs/Archetype.java) is a declaration of component composition used to create entities at runtime.
- **Transmutation**: a [Transmutation](https://github.com/JoannickGardize/SharkECS/blob/main/src/main/java/com/sharkecs/Transmutation.java) is the declaration of a possible mutation operation from an Archetype to another at runtime.
- **Aspect:** [Aspects](https://github.com/JoannickGardize/SharkECS/blob/main/src/main/java/com/sharkecs/aspect/Aspect.java) represents a group of possible entity composition (of component), Aspect declaration is made via annotation on classes implementing [Subscriber](https://github.com/JoannickGardize/SharkECS/blob/main/src/main/java/com/sharkecs/Subscriber.java).
- **Subscription**: a maintained collection of entity, generally of a given aspect. A subscription can be listened to get notified of insertion, removal, and mutation.
- **Subscriber**: a class interested to subscribe to a subscription (generally of a given aspect, via it's class annotation declaration). [SubscriberAdapter](https://github.com/JoannickGardize/SharkECS/blob/main/src/main/java/com/sharkecs/SubscriberAdapter.java) is a convenient base for this kind of class. Typical systems will subscribe to a subscription and iterate over its entities during processing.
- **Processor**: a processor is something that will be processed at each engine process call.
- **System**: systems are processors of a given entity aspect, they implement a part of the game logic related to this entity aspect. In SharkECS there is no explicit system base class to extends. Systems generally extends [SubscriberAdapter](https://github.com/JoannickGardize/SharkECS/blob/main/src/main/java/com/sharkecs/SubscriberAdapter.java) and implements [Processor](https://github.com/JoannickGardize/SharkECS/blob/main/src/main/java/com/sharkecs/SubscriberAdapter.java). [IteratingSystem](https://github.com/JoannickGardize/SharkECS/blob/main/src/main/java/com/sharkecs/IteratingSystem.java) is provided for convenience, and is the most common base class to use.
- **Manager**: manager is a general term of something providing shared behaviors / access to systems. The [EntityManager](https://github.com/JoannickGardize/SharkECS/blob/main/src/main/java/com/sharkecs/EntityManager.java) is the most relevant example.
- **Engine**: the root class, call ```Engine#process()``` in your main game loop. It is simply made of an array of Processor, since everything is wired during the engine building.

## Getting started

Let's take a simple, fictitious example of an engine made of player(s) and bullets that could damage them.

### System example

Most systems extends [IteratingSystem](https://github.com/JoannickGardize/SharkECS/blob/main/src/main/java/com/sharkecs/IteratingSystem.java), which iterates over all subscribed entity each process cycle. Let's assume that we have a ```PhysicsSystem```, updating a ```Physics``` component and its list of actually colliding other ```Physics```. The system responsible of damaging players with the bullets could looks like this:

```java
@WithAll({ Physics.class, Bullet.class })
public class BulletDamageSystem extends IteratingSystem {

	private EntityManager entityManager;

	private ComponentMapper<Physics> physicsMapper;

	private ComponentMapper<Bullet> bulletMapper;

	private ComponentMapper<Health> healthMapper;

	@Override
	public void process(int entityId) {
		Physics physics = physicsMapper.get(entityId);
		for (Physics colliding : physics.getColliding()) {
			int collidingId = colliding.getEntityId();
			Health health = healthMapper.get(collidingId);
			if (health != null) {
				Bullet bullet = bulletMapper.get(entityId);
				health.takeDamage(bullet.getDamage());
				entityManager.remove(entityId);
			}
		}
	}

	public void setEntityManager(EntityManager entityManager) {
		this.entityManager = entityManager;
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

The attributes of the system will be automatically injected during the engine building. Note the presence of setter methods: they are required and used for injection, because SharkECS is respectful of the encapsulation principle.

### EngineBuilder

[EngineBuilder](https://github.com/JoannickGardize/SharkECS/blob/main/src/main/java/com/sharkecs/builder/EngineBuilder.java) is used to configure and create the Engine:

```java
EngineBuilder builder = new EngineBuilder();

// Register component types
builder.component(Physics.class, Physics::new);
builder.component(Bullet.class, Bullet::new);
builder.component(Health.class, Health::new);

// Register entity archetypes, they are injected by name where required
builder.archetype("playerArchetype", Physics.class, Health.class);
builder.archetype("bulletArchetype", Physics.class, Bullet.class);

// Register systems, with order constraints
builder.with(new PhysicsSystem());
builder.then(new BulletDamageSystem());
builder.then(new HealthSystem());

// Register some singletons to be injected where required
builder.with(new Time());
builder.with(new ExternalPhysicsEngine());

Engine engine = builder.build();
```

Finally, call ```engine.process()``` in your main game loop.

## Priority management

You may want a specific execution order of your systems (or any sequentially executed elements: Processor, Initializable, Configurator...).

To achieve this, the naive way is to specify a priority number to elements, but this can lead to readability and maintainability issues.

In SharkECS, you specify priorities in the form of before / after constraints. There is different ways to do this:

- use `EngineBuilder#then(Object)` to register an element and add a "after" priority constraint between it and the previously registered element
- use `EngineBuilder#after(Object, Object...)` or `EngineBuilder#before(Object, Object...)` to add constraints between registered elements, elements could be:
  - the instance of the concerned element
  - a Class, every elements assignable to this Class will be concerned
  - any non-registered instance as a "marker" in the priority graph

For instance, the EngineBuilder's default configuration calls `builder.after(Processor.class, entityManager);` to put the EntityManager before any other Processor.

## Transmutation, injection by generic types, custom engine configurator...

This readme is not complete, to go deeper, see the javadoc of the code.
