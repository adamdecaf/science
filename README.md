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

## Why?



## License
