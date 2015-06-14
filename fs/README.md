# science/fs

This sub-project is used to allow science to store results to flat files where the code is running itself.

## How

This class uses the native `java.nio.file.Files` class to write `java.io.File`'s to disk. In order to use it you need to specify a base directory to store into.

```scala
import java.io.File

// Inside your application code
val base = new File(directory)

if (!base.exists) {
  base.mkdirs()
}

val storage = new FilesystemStorageStrategy(base)

// to run experiments
val experiment = Experiment(storage)
experiment(old(), new())
```
