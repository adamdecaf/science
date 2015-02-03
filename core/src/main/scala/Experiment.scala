package org.adamdecaf.scientist
import nl.grons.metrics.scala.InstrumentedBuilder

object Experiment {
  def apply[C, E, Storage](name: String)(control: C, candidate: => E)(implicit metricsBuilder: InstrumentedBuilder,
                                                                      controlSerializer: Serialization[C, Storage],
                                                                      candidateSerializer: Serialization[E, Storage],
                                                                      throwableSerializer: Serialization[Throwable, Storage],
                                                                      storage: StorageStrategy[Storage],
                                                                      strategy: ExperimentStrategy[E] = ExperimentStrategy.default): C = {
    val timer = metricsBuilder.metrics.timer(name)
    lazy val cache = timer.time(candidate)

    try {
      strategy.candidate(cache).foreach { trial =>
        val controlSerialized = controlSerializer.serialize(control)
        val candidateSerialized = candidateSerializer.serialize(trial)
        storage.store(controlSerialized, candidateSerialized)
      }
    } catch {
      case err: Throwable =>
        val controlSerialized = controlSerializer.serialize(control)
        storage.failed(controlSerialized, err)
    }

    control
  }
}
