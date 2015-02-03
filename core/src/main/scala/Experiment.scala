package org.adamdecaf.scientist
import nl.grons.metrics.scala.InstrumentedBuilder

object Experiment {
  def apply[C, E](name: String)(control: C, candidate: => E)(implicit metricsBuilder: InstrumentedBuilder,
                                                             storage: StorageStrategy[C, E],
                                                             strategy: ExperimentStrategy[E] = ExperimentStrategy.default): C = {
    val timer = metricsBuilder.metrics.timer(name)
    lazy val cache = timer.time(candidate)

    try {
      strategy.experiment(cache).foreach(storage.store(control, _))
    } catch {
      case err: Throwable =>
        storage.failed(control, err)
    }

    control
  }
}
