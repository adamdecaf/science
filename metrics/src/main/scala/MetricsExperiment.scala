package org.decaf.science.metrics
import org.decaf.science._
import io.dropwizard.metrics._

object MetricsExperiment {
  def apply[Control, Exp, Storage](registry: MetricRegistry)(name: String, experiment: Experiment[Control, Exp, Storage]): Experiment[Control, Exp, Storage] = {
    val controlMeter: Meter = new Meter()
    val experimentMeter: Meter = new Meter()
    val exceptionsDuringExperimentsMeter: Meter = new Meter()

    registry.register(s"${name}.control", controlMeter)
    registry.register(s"${name}.experiment", experimentMeter)
    registry.register(s"${name}.exceptions-during-experiments", exceptionsDuringExperimentsMeter)

    val after = new AfterExperiment {
      def afterControl(): Unit = controlMeter.mark()
      def afterExperiment(): Unit = experimentMeter.mark()
      def afterExceptionInExperiment(): Unit = exceptionsDuringExperimentsMeter.mark()
    }

    experiment.withAfterExperiment(after)
  }
}
