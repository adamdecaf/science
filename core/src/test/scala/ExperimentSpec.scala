package org.decaf.science
import org.specs2.specification.Scope
import org.specs2.mutable.Specification

object ExperimentSpec extends Specification {
  import Serialization.Strings._

  "always run the experiment" in new context {
    (1 to 10) foreach { _ =>
      alwaysExperiment(incrementControl(), incrementExperiment())
    }

    controlCounter must be_==(10)
    experimentCounter must be_==(10)

    storage.getAllControlResults() must have size(10)
    storage.getAllExperimentResults() must have size(10)
    storage.getAllFailedResults() must beEmpty
  }

  "never run the experiment" in new context {
    (1 to 10) foreach { _ =>
      neverExperiment(incrementControl(), incrementExperiment())
    }

    controlCounter must be_==(10)
    experimentCounter must be_==(0)

    storage.getAllControlResults() must have size(10)
    storage.getAllExperimentResults() must beEmpty
    storage.getAllFailedResults() must beEmpty
  }

  "record failed experiments" in new context {
    (1 to 10) foreach { _ =>
      alwaysExperiment(incrementControl(), failingExperiment())
    }

    controlCounter must be_==(10)
    experimentCounter must be_==(0)

    storage.getAllFailedResults() must have size(10)
  }

  trait context extends Scope {
    var controlCounter = 0
    var experimentCounter = 0

    def incrementControl(): Int = {
      controlCounter += 1
      controlCounter
    }

    def incrementExperiment(): Int = {
      experimentCounter += 1
      experimentCounter
    }

    def failingExperiment(): Int = throw new Exception("boom")

    val storage = new InMemoryStorageStrategy[String]
    lazy val alwaysExperiment = Experiment(storage)(ExperimentStrategy.always[Int])
    lazy val neverExperiment = Experiment(storage)(ExperimentStrategy.never[Int])
  }
}
