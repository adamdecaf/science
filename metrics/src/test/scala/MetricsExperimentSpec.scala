package org.decaf.science.metrics
import org.decaf.science._
import org.specs2.specification.Scope
import org.specs2.mutable.Specification
import io.dropwizard.metrics.{Meter, MetricRegistry, Timer}

object MetricsExperimentSpec extends Specification {

  "record in metrics when experiments pass through" in new context {
    (1 to 10) foreach { i =>
      always(control(), experiment())
    }

    storage.getAllControlResults() must have size(10)

    val meter1: Meter = registry.meter("test-metrics-experiment.control")
    val meter2: Meter = registry.meter("test-metrics-experiment.experiment")
    val meter3: Meter = registry.meter("test-metrics-experiment.exceptions-during-experiments")

    meter1.getCount() must be_==(10)
    meter2.getCount() must be_==(10)
    meter3.getCount() must be_==(0)

    val timer1: Timer = registry.timer("test-metrics-experiment.control-timer")
    val timer2: Timer = registry.timer("test-metrics-experiment.experiment-timer")

    timer1.getCount() must be_==(10)
    timer2.getCount() must be_==(10)

    controlCount must be_==(10)
    experimentCount must be_==(10)
  }

  "mark a failure during experiments" in new context {
    always(control(), failing())
    storage.getAllControlResults() must beEmpty
    val meter: Meter = registry.meter("test-metrics-experiment.exceptions-during-experiments")
    meter.getCount() must be_==(1)
  }

  trait context extends Scope {
    var controlCount: Int = 0
    var experimentCount: Int = 0

    def control(): String = {
      controlCount += 1
      "control"
    }

    def experiment(): String = {
      experimentCount += 1
      "experiment"
    }

    def failing(): String = throw new Exception("nope")

    val registry = new MetricRegistry()
    val storage = new InMemoryStorageStrategy[String]
    val exp = Experiment(storage)(ExperimentStrategy.always[String])
    val always = MetricsExperiment(registry)("test-metrics-experiment", exp)
  }
}
