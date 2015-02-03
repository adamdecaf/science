package org.adamdecaf.scientist

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
