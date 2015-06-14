package org.decaf.science.fs
import org.decaf.science._
import java.io.File
import org.specs2.specification.Scope
import org.specs2.mutable.Specification

object FilesystemStorageStrategySpec extends Specification {
  import org.decaf.science.Serialization.Strings._

  "store control and experiment results in files" in new context {
    override lazy val name = "always"

    (1 to 10) foreach { i =>
      experiment(controlFunction(i), experimentFunction(i))
    }

    val fileNames = getFileNames()
    val (controlNames, experimentNames) = fileNames.partition(_.endsWith("-control"))

    experimentNames.forall(_.endsWith("-experiment")) must beTrue

    controlNames must have size(10)
    experimentNames must have size(10)
  }

  "store control and failed experiment results in files" in new context {
    override lazy val name = "failing"

    (1 to 10) foreach { i =>
      experiment(controlFunction(i), failingFunction(i))
    }

    val fileNames = getFileNames()
    val (controlNames, failingNames) = fileNames.partition(_.endsWith("-control"))

    failingNames.forall(_.endsWith("-failed")) must beTrue

    controlNames must have size(10)
    failingNames must have size(10)
  }

  trait context extends Scope {
    def controlFunction(i: Int): Int = 10 + i
    def experimentFunction(i: Int): Int = 100 + i
    def failingFunction(i: Int): Int = throw new Exception("boom")

    def name: String = ""
    val now = System.currentTimeMillis().toString
    def directory = s"fs-test/${now}-${name}/"
    val base = new File(directory)

    if (!base.exists) {
      base.mkdirs()
    }

    val storage = new FilesystemStorageStrategy(base)
    val experiment = Experiment(storage)(ExperimentStrategy.always[Int])

    def getFileNames(dir: String = directory): List[String] = {
      val normalized = if (dir.endsWith("/")) dir else s"${dir}/"
      val where = new File(normalized)
      val fileArray = where.listFiles()
      if (fileArray != null) {
        fileArray.iterator.toList.map(_.getName())
      } else {
        List.empty
      }
    }
  }
}
