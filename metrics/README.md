# science/metrics

This sub-project wraps [dropwizard metrics](https://github.com/dropwizard/metrics) integration around your experiments.

## How

Given a typical experiment you can wrap it in a metrics class.

```scala
import org.decaf.science._
import org.decaf.science.metrics._
import io.dropwizard.metrics.MetricRegistry

object Test {
  val registry = new MetricRegistry()

  // Please use a different storage strategy.
  val storage = new InMemoryStorageStrategy[String]
  val exp = Experiment(storage)(ExperimentStrategy.always[String])
  val always = MetricsExperiment(registry)("test-metrics-experiment", exp)

  // Run your experiment like normal
  // always(control(), experiment())
}
```

## Metrics Created

For each `MetricsExperiment` you need to define a name, this is combined with the following suffixes `control`, `experiment`, and `exceptions-during-experiments` to form three metrics.

So, if you had an experiment called `test-experiment` your three metrics would be called.

- `test-experiment.control`
- `test-experiment.experiment`
- `test-experiment.exceptions-during-experiments`

Also, timers will be created under the following names.

- `test-experiment.control-timer`
- `test-experiment.experiment-timer`
