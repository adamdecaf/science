package org.decaf.science
import scala.util.control.NonFatal

object Experiment {
  def apply[Control, Exp, Storage]
    (storageStrategy: StorageStrategy[Storage])
    (control: => Control, candidate: => Exp)
    (implicit
       controlSerializer: Serialization[Control, Storage],
       candidateSerializer: Serialization[Exp, Storage],
       throwableSerializer: Serialization.ThrowableSerialization[Storage],
       experimentStrategy: ExperimentStrategy[Exp] = ExperimentStrategy.default
    ) =
    new Experiment(control, candidate, controlSerializer, candidateSerializer, throwableSerializer, storageStrategy, experimentStrategy)
}

class Experiment[Control, Exp, Storage] private(
  control: Control,
  candidate: => Exp,
  controlSerializer: Serialization[Control, Storage],
  candidateSerializer: Serialization[Exp, Storage],
  throwableSerializer: Serialization.ThrowableSerialization[Storage],
  storageStrategy: StorageStrategy[Storage],
  experimentStrategy: ExperimentStrategy[Exp]
) {
  def run(): Control = {
    try {
      experimentStrategy.experiment(candidate).foreach { trial =>
        val controlSerialized = controlSerializer.serialize(control)
        val candidateSerialized = candidateSerializer.serialize(trial)
        storageStrategy.store(controlSerialized, candidateSerialized)
      }
    } catch {
      case NonFatal(err) =>
        storageStrategy.failed(controlSerializer.serialize(control), err)(throwableSerializer)
    }

    control
  }
}
