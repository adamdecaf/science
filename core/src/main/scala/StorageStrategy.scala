package org.decaf.science

trait StorageStrategy[S] {
  def store(control: S, experiment: Option[S] = None): Unit
  def failed[T <: Throwable](control: S, experiment: T)(implicit serializer: Serialization[T, S]): Unit
}

object StorageStrategy {
  def empty[S]: StorageStrategy[S] = new StorageStrategy[S] {
    def store(control: S, experiment: Option[S] = None): Unit = {}
    def failed[T <: Throwable](control: S, experiment: T)(implicit serializer: Serialization[T, S]): Unit = {}
  }
}
