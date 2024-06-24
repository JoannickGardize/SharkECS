# SharkECS

*Development in progress, may drastically change other time.*

SharkECS is a lightweight Entity-Component-System implementation focused on performance, ease of use, and flexibility.

The specificity of SharkECS regarding other ECS frameworks is that all possible entity composition and mutation must be
declared first. In this way, performance is easily optimized.

## Terminology

- **Entity**: an entity is something made of components. Entities by themselves are just an integer identifier. Entity
  creation, deletion, and mutation operations are made via
  the [EntityManager](https://github.com/JoannickGardize/SharkECS/blob/main/src/main/java/com/sharkecs/EntityManager.java).
  Note that entity IDs are not unique other time.
- **Component**: components are data holders representing something some entities are made of (position, body, health,
  AI, sprite...). Components are accessed
  via [ComponentMappers](https://github.com/JoannickGardize/SharkECS/blob/main/src/main/java/com/sharkecs/ComponentMapper.java).
- **Archetype**:
  an [Archetype](https://github.com/JoannickGardize/SharkECS/blob/main/src/main/java/com/sharkecs/Archetype.java) is a
  declaration of component composition used to create entities at runtime.
- **Transmutation**:
  a [Transmutation](https://github.com/JoannickGardize/SharkECS/blob/main/src/main/java/com/sharkecs/Transmutation.java)
  is the declaration of a possible mutation operation from an Archetype to another one at runtime.
- **Aspect:** [Aspects](https://github.com/JoannickGardize/SharkECS/blob/main/src/main/java/com/sharkecs/Aspect.java)
  are a kind of filter of entity regarding their component composition. Aspect declaration is made via annotation on
  classes
  implementing [Subscriber](https://github.com/JoannickGardize/SharkECS/blob/main/src/main/java/com/sharkecs/Subscriber.java).
- **Subscription**: a maintained collection of entity, generally of a given aspect. A subscription can be listened to
  get notified of insertion, removal, and mutation.
- **Subscriber**: a class interested to subscribe to a subscription (generally of a given aspect, via its class
  annotation
  declaration). [SubscriberAdapter](https://github.com/JoannickGardize/SharkECS/blob/main/src/main/java/com/sharkecs/SubscriberAdapter.java)
  is a convenient base for this kind of class. Typical systems will subscribe to a subscription and iterate over its
  entities during processing.
- **Processor**:
  a [Processor](https://github.com/JoannickGardize/SharkECS/blob/main/src/main/java/com/sharkecs/Processor.java) is
  something that will be processed at each engine process call.
- **System**: systems are processors of a given entity aspect, they implement a part of the game logic related to this
  entity aspect. In SharkECS, there is no explicit system base class to extends. Systems generally
  extends [SubscriberAdapter](https://github.com/JoannickGardize/SharkECS/blob/main/src/main/java/com/sharkecs/SubscriberAdapter.java)
  and
  implements [Processor](https://github.com/JoannickGardize/SharkECS/blob/main/src/main/java/com/sharkecs/SubscriberAdapter.java). [IteratingSystem](https://github.com/JoannickGardize/SharkECS/blob/main/src/main/java/com/sharkecs/IteratingSystem.java)
  is provided for convenience, and is the most common base class to use.
- **Manager**: manager is a general term of something providing shared behaviors / access to systems.
  The [EntityManager](https://github.com/JoannickGardize/SharkECS/blob/main/src/main/java/com/sharkecs/EntityManager.java)
  is the most relevant example.
- **Engine**: the root class, call `Engine#process()` in your main game loop. It is simply made of an array of
  Processor, since everything is wired during the engine building.

## Getting started

Let's take an example of an engine made of player(s) and bullets that could damage them.

### System example

Most systems
extends [IteratingSystem](https://github.com/JoannickGardize/SharkECS/blob/main/src/main/java/com/sharkecs/IteratingSystem.java),
which iterates over all subscribed entity each process cycle (i.e. each frames).
Let's assume that we have a `PhysicsSystem`, updating a `Physics` component and its list of actually colliding
other `Physics`. The system responsible for damaging players with the bullets could look like this:

```java

@WithAll({Physics.class, Bullet.class})
public class BulletDamageSystem extends IteratingSystem {

    private ComponentMapper<Physics> physicsMapper;
    private ComponentMapper<Bullet> bulletMapper;
    private ComponentMapper<Health> healthMapper;

    @Override
    public void process(int entityId) {
        Physics physics = physicsMapper.get(entityId);
        for (EntityReference entityRef : physics.getCollisionGroup()) {
            entityRef.ifExists(collidingId ->
                    healthMapper.ifExists(collidingId, health -> {
                        Bullet bullet = bulletMapper.get(entityId);
                        health.takeDamage(bullet.getDamage());
                        entityManager.remove(entityId);
                    }));
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

According to the @WithAll annotation, this system will process all entities with at least a Physics and a Bullet
component. The system will apply the bullet's damage once it hit a damageable (with a Health component) entity, and then
remove the bullet entity.

The attributes of the system will be automatically injected during the engine building. Note the presence of setter
methods: they are required and used for injection. If you feel uncomfortable with this boilerplate code, Take a look at
the [Project Lombok](https://projectlombok.org/).

You can find the full running
example [here](https://github.com/JoannickGardize/SharkECS/tree/main/src/test/java/com/sharkecs/example).

### EngineBuilder

[EngineBuilder](https://github.com/JoannickGardize/SharkECS/blob/main/src/main/java/com/sharkecs/builder/EngineBuilder.java)
is used to configure and create the Engine:

```java
EngineBuilder builder = EngineBuilder.withDefaults()

        // Register component types
        .component(Physics.class, Physics::new)
        .component(Bullet.class, Bullet::new)
        .component(Health.class, Health::new)
        .component(Shooter.class, Shooter::new)
        .component(Corpse.class, Corpse::new)

        // Register entity archetypes
        .archetype("player", Physics.class, Health.class, Shooter.class)
        .archetype("corpse", Physics.class, Corpse.class)
        .archetype("bullet", Physics.class, Bullet.class)

        // Register transmutations
        .transmutation("player", "corpse")

        // Register managers & systems, in the right order
        .with(new TimeManager())
        .then(new PhysicsSystem())
        .then(new BulletDamageSystem())
        .then(new BulletLifetimeSystem())
        .then(new ShootSystem())
        .then(new DeathSystem())

        // Register miscellaneous stuff
        .with(new Time())
        .with(new Viewport())
        .with(new ExampleScenarioInitializer());

Engine engine = builder.build();
```

Finally, call `engine.process()` in your main game loop.

The full and runnable code of this example can be
found [here](https://github.com/JoannickGardize/SharkECS/tree/main/src/test/java/com/sharkecs/example)

## Entity references

Since entity IDs are not unique over time, this is not possible to reference an entity by using a simple integer.
Instead, `EntityManager#reference(int)` provides
an [EntityReference](https://github.com/JoannickGardize/SharkECS/blob/main/src/main/java/com/sharkecs/EntityReference.java)
instance, properly cleared when the referenced entity is removed.

## Priority management

You may want a specific execution order of your systems (or any sequentially executed elements: Processor,
Initializable, Configurator...).

To achieve this, the naive way is to specify a priority number to elements, but this can lead to readability and
maintainability issues.

In SharkECS, you specify priorities in the form of before / after constraints. There is different ways to do this:

- `EngineBuilder#then(Object)` is a convenience method to register an element **and** add an "after" priority constraint
  between it and the previously registered element
- `EngineBuilder#after(Object, Object...)` and `EngineBuilder#before(Object, Object...)` add constraints between
  registered elements, parameters could be:
    - the instance of the concerned element
    - a Class, every element assignable to this Class will be concerned
    - an annotation type, every element declaring this annotation will be concerned
    - any non-registered instance as a "marker" in the priority graph

For instance, the EngineBuilder's default configuration calls `builder.before(entityManager, Processor.class);` to put
the EntityManager before any other Processor.

## Add and remove single components

This is common in an ECS architecture to make use of "volatile" components, to plug and unplug on the fly temporary
behaviors to entities.

This kind of component usage is unusual in a naive way for this framework, due to the declaration requirement, and lead
to an exponential amount of archetype and transmutation declaration requirement at the engine building step.

This is
where [ArchetypeVariantsBuilder](https://github.com/JoannickGardize/SharkECS/blob/main/src/main/java/com/sharkecs/builder/ArchetypeVariantsBuilder.java)
can be used at the engine building step to declare all required archetypes and transmutations.
See [ArchetypeVariantsBuilderTest](https://github.com/JoannickGardize/SharkECS/blob/main/src/test/java/com/sharkecs/builder/ArchetypeVariantsBuilderTest.java)
for an example code.

In addition to this, archetypes with one component of difference are treated specifically, allowing the use
of `EntityManager#addComponent(...)` and `EntityManager#removeComponent(...)` to achieve the transmutations.

## Transmutation, injection by generic types, custom engine configurator...

This readme is not complete, to go deeper, see the javadoc of the code.
