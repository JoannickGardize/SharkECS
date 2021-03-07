# SharkECS
Entity Component System implementation focused on performance good design, and ease of use.

The specificity of SharkECS regarding to other ECS frameworks is that all possible entity composition and mutation must be declared first. In this way, performance is easily optimized.

## Terminology

- **Entity**: entities are simple integer IDs representing something made of components. Entity creation, deletion, and mutation operations are made via the [EntityManager](https://github.com/JoannickGardize/SharkECS/blob/main/src/main/java/com/sharkecs/EntityManager.java).
- **Component**: Components are pure data holders representing an aspect of entities (position, body, health, sprite...). Components are accessed via [ComponentMappers](https://github.com/JoannickGardize/SharkECS/blob/main/src/main/java/com/sharkecs/ComponentMapper.java).
- **Archetype**: an [Archetype](https://github.com/JoannickGardize/SharkECS/blob/main/src/main/java/com/sharkecs/Archetype.java) is a declaration of component composition used to create entities at runtime.
- **Transmutation**: a [Transmutation](https://github.com/JoannickGardize/SharkECS/blob/main/src/main/java/com/sharkecs/Transmutation.java) is the declaration of a possible mutation from an Archetype to another.
- **Aspect:** [Aspects](https://github.com/JoannickGardize/SharkECS/blob/main/src/main/java/com/sharkecs/aspect/Aspect.java) represents a group of possible entity composition (of component), Aspect declaration is made via annotation on classes implementing [Subscriber](https://github.com/JoannickGardize/SharkECS/blob/main/src/main/java/com/sharkecs/Subscriber.java).
- **Subscription**: Is a maintained collection of entity, generally of a given aspect. A subscription can be listened to get notified of insertions, removal, and mutations. Typical systems will subscribe to a subscription and iterate over its entities during processing.
- **Subscriber**: a class interested to subscribe to a subscription of a given aspect (via it's class annotation declaration). [SubscriberAdapter](https://github.com/JoannickGardize/SharkECS/blob/main/src/main/java/com/sharkecs/SubscriberAdapter.java) is a convenient base for this kind of class.
- **Processor**: a processor is something than will be processed each engine process call.
- **System**: systems are processors of a given entity aspect, they implement a part of the game logic related to this entity aspect. In SharkECS there is no explicit system base class to extends. Systems generally extends [SubscriberAdapter](https://github.com/JoannickGardize/SharkECS/blob/main/src/main/java/com/sharkecs/SubscriberAdapter.java) and implements [Processor](https://github.com/JoannickGardize/SharkECS/blob/main/src/main/java/com/sharkecs/SubscriberAdapter.java). [IteratingSystem](https://github.com/JoannickGardize/SharkECS/blob/main/src/main/java/com/sharkecs/IteratingSystem.java) is provided for convenience, and is the most common base class to use.
- **Engine**: the root class, call #process() in your main game loop. It is simply made of an array of Processor, because everything is wired during the engine building.

## Getting started

Let's take a simple, fictitious example of an engine made of player(s) and bullets that could damage them.

### Systems

Most systems extends [IteratingSystem](https://github.com/JoannickGardize/SharkECS/blob/main/src/main/java/com/sharkecs/IteratingSystem.java), which iterates over all subscribed entity each process cycle. Let's assume that we have a ```PhysicsSystem```, updating a ```Physics``` component and its list of colliding other ```Physics```. The system responsible of damaging players with the bullets could looks like this:
```java
@WithAll({ Physics.class, Health.class })
public class ImpactDamageSystem extends IteratingSystem {

	private EntityManager entityManager;

	private ComponentMapper<Physics> physicsMapper;

	private ComponentMapper<Health> healthMapper;

	private ComponentMapper<Bullet> bulletMapper;

	@Override
	public void process(int entityId) {
		Physics physics = physicsMapper.get(entityId);
		for (Physics colliding : physics.getColliding()) {
			int collidingId = colliding.getEntityId();
			Bullet bullet = bulletMapper.get(collidingId);
			if (bullet != null) {
				healthMapper.get(entityId).takeDamage(bullet.getDamage());
				entityManager.remove(collidingId);
			}
		}
	}

	public void setEntityManager(EntityManager entityManager) {
		this.entityManager = entityManager;
	}

	public void setPhysicsMapper(ComponentMapper<Physics> physicsMapper) {
		this.physicsMapper = physicsMapper;
	}

	public void setHealthMapper(ComponentMapper<Health> healthMapper) {
		this.healthMapper = healthMapper;
	}

	public void setBulletMapper(ComponentMapper<Bullet> bulletMapper) {
		this.bulletMapper = bulletMapper;
	}
}
```
The attributes of the system will be automatically injected during the engine building. Note the presence of setters for attributes: they are required and used for injection, because SharkECS is respectful of the encapsulation principle.

### EngineBuilder

[EngineBuilder](https://github.com/JoannickGardize/SharkECS/blob/main/src/main/java/com/sharkecs/builder/EngineBuilder.java) is used to configure and create the Engine, 
```java
EngineBuilder builder = new EngineBuilder();

// Register component types
builder.component(Physics.class, Physics::new);
builder.component(Bullet.class, Bullet::new);
builder.component(Health.class, Health::new);

// Register entity archetypes, they are injected by name where required
builder.archetype("playerArchetype", Physics.class, Health.class);
builder.archetype("bulletArchetype", Physics.class, Bullet.class);

// Register systems
builder.with(new PhysicsSystem());
builder.with(new ImpactDamageSystem());
builder.with(new HealthSystem());

// Register some singletons to be injected where required
builder.with(new Time());
builder.with(new ExternalPhysicsEngine());

Engine engine = builder.build();
```
Then, call ```engine.process()``` in your main game loop.

### Transmutation, injection by generic types, custom engine configurator

This readme is not complete, to go deeper, see the javadoc of the code.

