package org.adamdecaf.scientist
import scala.util.Random

trait ExperimentStrategy[T] {
  def experiment(candidate: => T): Option[T]
}

object ExperimentStrategy {
  def default[T]: ExperimentStrategy[T] = new ExperimentStrategy[T] {
    private[this] val random = new Random()

    def experiment(candidate: => T): Option[T] =
      if (random.nextBoolean()) {
        Some(candidate)
      } else {
        None
      }
  }
}
