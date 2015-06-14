# science

> Allowing experiments in program logic to be studied and tested.

## Usage

Add the following to your project's build definition.

```scala
libraryDependencies ++= Seq(
  "org.decaf" %% "science" % "1.0.0"
)
```

```scala
import org.decaf.science._

// You'll want to pick a different storage
val storage = new InMemoryStorageStrategy[String]
val experiment = Experiment(storage)

// Your application logic
def old(): Int = ???
def new(): Double = ???

val result: Int = experiment(old(), new())
```

## How it works

Two blocks of code are executed, which are a control and an "experiment". Each is saved off to a storage medium for later analysis. This allows you to compare directly the differences between the changes made in your application code. In order to maintain you existing types and flow the control block is returned so your application is able to continue on unchanged.

## Storage Strategies

- [fs](fs/) (raw filesystem storage)
- [In Memory](core/src/main/scala/InMemoryStorageStrategy.scala) (useful for basic testing) -- **not threadsafe**

## Issues

Please make a github issue with your issue. I'll work with you to get a fix implemented and released. Thanks!

## Contributing

1. Make a github issue or ping me on twitter ([@adamdecaf](https://twitter.com/adamdecaf)) so we don't duplicate work.
1. Fork project to your organization / account
1. Submit PR with passing tests.
1. I'll merge it!

## Contributors

- Adam Shannon ([@adamdecaf](https://twitter.com/adamdecaf))

## License

Apache 2.0 - See [LICENSE](LICENSE)
