package org.adamdecaf.scientist

trait StorageStrategy[C, E] {
  def store(control: C, experiment: E): Unit
  def failed[T <: Throwable](control: C, experiment: T): Unit
}
