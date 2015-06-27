package org.decaf.science.metrics
import org.decaf.science._
import io.dropwizard.metrics._
import java.util.Iterator

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

      val controlMeter = findAndRegisterMeter(s"${name}.control")
      val experimentMeter = findAndRegisterMeter(s"${name}.experiment")
      val exceptionsDuringExperimentsMeter = findAndRegisterMeter(s"${name}.exceptions-during-experiments")

      val controlTimer = findAndRegisterTimer(s"${name}.control-timer")
      val experimentTimer = findAndRegisterTimer(s"${name}.experiment-timer")

      val during = new DuringExperiment[Control, Exp] {
        def duringControl(exp: => Control): Control = {
          val context = controlTimer.time
          try {
            exp
          } finally {
            context.stop
          }
        }
        def duringExperiment(exp: => Exp): Exp = {
          val context = experimentTimer.time
          try {
            exp
          } finally {
            context.stop
          }
        }
      }

      val after = new AfterExperiment {
        def afterControl(): Unit = controlMeter.mark()
        def afterExperiment(): Unit = experimentMeter.mark()
        def afterExceptionInExperiment(): Unit = exceptionsDuringExperimentsMeter.mark()
      }

      parent.withDuringExperiment(during).withAfterExperiment(after).run()
    }

    private[this] def findAndRegisterTimer(name: String): Timer = {
      val found = registry.getTimers(new NameBasedMetricFilter(name)).values.iterator
      findAndRegisterMetric(name, new Timer(), found)
    }

    private[this] def findAndRegisterMeter(name: String): Meter = {
      val found = registry.getMeters(new NameBasedMetricFilter(name)).values.iterator
      findAndRegisterMetric(name, new Meter(), found)
    }

    private[this] def findAndRegisterMetric[T <: Metric](name: String, metric: => T, found: Iterator[T]): T = {
      if (found != null && found.hasNext()) {
        found.next()
      } else {
        val m = metric
        registry.register(name, m)
        m
      }
    }

    private[this] class NameBasedMetricFilter(needle: String) extends MetricFilter {
      def matches(name: MetricName, metric: Metric): Boolean = name.getKey() equalsIgnoreCase needle
    }
  }
}
