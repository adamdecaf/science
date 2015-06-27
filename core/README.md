# science-core

This is the common classes and types used to abstract over the different experiment conditions and storage mediums. This library is intentionally left small to maintain a small footprint in order to use science!

### Components

__Experiment__

This class is just the builder setup in order to make sure everything needed is in scope (experiment strategy, storage, serializers). The overloading of `apply(..)` methods here is messy to keep the rest of the classes cleaner.

__ExperimentStrategy__

This class answers the question "Given some outside (read: side-effecty) state determine if a block should be executed". The default strategy here is to use `scala.util.Random` in order to determine on a pesudorandom basis if an experiment should be used. You can easily extend this out to a boolean predicate from your application logic.

```scala
// Define something like this in another class of yours
val strategy = ExperimentStrategy.boolean(config.getBoolean("new-user-feature-enabled"))
```

__Serialization__

You have your types, we have ours. To keep things fair science takes no preference over libraries, types, or classes used. If there's no support for your types you can probably submit a PR to have it added as part of a sub-module library.

__StorageStrategy__

This class represents the logic for storing serialized types (often `String` or `Array[Byte]`) into the proper storage medium. This can be databases, flat files, or even in memory (that's not recommended however).
