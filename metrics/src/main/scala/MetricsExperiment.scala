package org.decaf.science.metrics
import org.decaf.science._
import io.dropwizard.metrics._

object MetricsExperiment {
  def apply[Storage, Exp](registry: MetricRegistry)(name: String, exp: ExperimentFromStorageAndStrategyBuilder[Storage, Exp]): ExperimentFromStorageAndStrategyBuilder[Storage, Exp] =
    new MetricsExperimentBuilder(registry, name, exp)

  class MetricsExperimentBuilder[Storage, Exp](registry: MetricRegistry, name: String, exp: ExperimentFromStorageAndStrategyBuilder[Storage, Exp])
      extends ExperimentFromStorageAndStrategyBuilder[Storage, Exp](exp.storage, exp.experimentStrategy) {

    override def apply[Control](control: => Control, candidate: => Exp)(implicit
                                                                        controlSerializer: Serialization[Control, Storage],
                                                                        candidateSerializer: Serialization[Exp, Storage],
                                                                        throwableSerializer: Serialization.ThrowableSerialization[Storage]): Control = {
      val parent = new Experiment(control, candidate, controlSerializer, candidateSerializer, throwableSerializer, storage, experimentStrategy)

      val controlMeter: Meter = findAndRegisterMeter(s"${name}.control")
      val experimentMeter: Meter = findAndRegisterMeter(s"${name}.experiment")
      val exceptionsDuringExperimentsMeter: Meter = findAndRegisterMeter(s"${name}.exceptions-during-experiments")

      val after = new AfterExperiment {
        def afterControl(): Unit = controlMeter.mark()
        def afterExperiment(): Unit = experimentMeter.mark()
        def afterExceptionInExperiment(): Unit = exceptionsDuringExperimentsMeter.mark()
      }

      parent.withAfterExperiment(after).run()
    }

    private[this] def findAndRegisterMeter(name: String): Meter = {
      val found = registry.getMeters(new NameBasedMetricFilter(name)).values.iterator
      if (found != null && found.hasNext()) {
        found.next()
      } else {
        val meter = new Meter()
        registry.register(name, meter)
        meter
      }
    }

    private[this] class NameBasedMetricFilter(needle: String) extends MetricFilter {
      def matches(name: MetricName, metric: Metric): Boolean = name.getKey() equalsIgnoreCase needle
    }
  }
}
