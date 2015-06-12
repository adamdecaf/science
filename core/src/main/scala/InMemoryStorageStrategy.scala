package org.decaf.science

// :siren:
// This class is not thread safe, you've been warned.
// :siren:

class InMemoryStorageStrategy[S] extends StorageStrategy[S] {
  def store(control: S, experiment: Option[S]): Unit = {
    controlStorage += control
    experiment.foreach(experimentStorage += _)
  }

  def failed[T <: Throwable](control: S, experiment: T)(implicit serializer: Serialization[T, S]): Unit =
    failedStorage += serializer.serialize(experiment)

  // InMemoryStorageStrategy specific methods
  def getAllControlResults(): List[S] = controlStorage.result
  def getAllExperimentResults(): List[S] = experimentStorage.result
  def getAllFailedResults(): List[S] = failedStorage.result

  private[this] val controlStorage = List.newBuilder[S]
  private[this] val experimentStorage = List.newBuilder[S]
  private[this] val failedStorage = List.newBuilder[S]
}
