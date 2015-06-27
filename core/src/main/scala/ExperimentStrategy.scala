package org.decaf.science
import scala.util.Random

trait ExperimentStrategy[T] {
  def experiment(candidate: => T): Option[T]
}

object ExperimentStrategy {
  def default[T]: ExperimentStrategy[T] = random[T]

  def random[T]: ExperimentStrategy[T] = new ExperimentStrategy[T] {
    private[this] val random = new Random()

    def experiment(candidate: => T): Option[T] =
      if (random.nextBoolean()) {
        Some(candidate)
      } else {
        None
      }
  }

  def boolean[T](cond: => Boolean): ExperimentStrategy[T] = new ExperimentStrategy[T] {
    def experiment(candidate: => T): Option[T] =
      if (cond) {
        Some(candidate)
      } else {
        None
      }
  }

  def always[T]: ExperimentStrategy[T] = new ExperimentStrategy[T] {
    def experiment(candidate: => T): Option[T] = Some(candidate)
  }

  def never[T]: ExperimentStrategy[T] = new ExperimentStrategy[T] {
    def experiment(candidate: => T): Option[T] = None
  }
}
