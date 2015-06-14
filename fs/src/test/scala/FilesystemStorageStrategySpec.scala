package org.decaf.science.fs
import org.decaf.science._
import java.io.File
import org.specs2.specification.Scope
import org.specs2.mutable.Specification

object FilesystemStorageStrategySpec extends Specification {
  import org.decaf.science.Serialization.Strings._

  "store control and experiment results in files" in new context {
    (1 to 10) foreach { _ =>
      experiment(controlFunction(), experimentFunction())
    }

    ok
  }

  "store control and failed experiment results in files" in pending

  trait context extends Scope {
    def controlFunction(): Int = 10
    def experimentFunction(): Int = 11

    val now = getNanosAsString()
    val directory = s"fs-test/${now}/"
    val base = new File(directory)

    if (!base.exists) {
      base.mkdirs()
    }

    val storage = new FilesystemStorageStrategy(base)
    val experiment = Experiment(storage)(ExperimentStrategy.always[Int])

    def getNanosAsString(): String = System.currentTimeMillis().toString
  }
}
