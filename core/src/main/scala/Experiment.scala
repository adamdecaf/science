package science
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

trait ExperimentStrategy[T] {
  def experiment(candidate: => T): Option[T]
}

object ExperimentStrategy {
  import scala.util.Random
  private[this] val random = new Random()

  def default[T]: ExperimentStrategy[T] = new ExperimentStrategy[T] {
    def experiment(candidate: => T): Option[T] =
      if (random.nextBoolean()) {
        Some(candidate)
      } else {
        None
      }
  }
}

trait StorageStrategy[C, E] {
  def store(control: C, experiment: E): Unit
  def failed[T <: Throwable](control: C, experiment: T): Unit
}
