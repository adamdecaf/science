package org.decaf.science

trait StorageStrategy[S] {
  def store(control: S, experiment: S): Unit
  def failed[T <: Throwable](control: S, experiment: T)(implicit serializer: Serialization[T, S]): Unit
}
