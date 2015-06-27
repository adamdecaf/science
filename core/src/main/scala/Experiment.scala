package org.decaf.science
import scala.util.control.NonFatal

object Experiment {
  def apply[Storage](storageStrategy: StorageStrategy[Storage]) = new ExperimentBuilder(storageStrategy)
}

class ExperimentBuilder[Storage](storage: StorageStrategy[Storage]) {
  def apply[Control, Exp](control: => Control, candidate: => Exp)(implicit
                                                                  controlSerializer: Serialization[Control, Storage],
                                                                  candidateSerializer: Serialization[Exp, Storage],
                                                                  throwableSerializer: Serialization.ThrowableSerialization[Storage],
                                                                  experimentStrategy: ExperimentStrategy[Exp] = ExperimentStrategy.default): Control =
    new Experiment(control, candidate, controlSerializer, candidateSerializer, throwableSerializer, storage, experimentStrategy).run()

  def apply[Exp](strategy: ExperimentStrategy[Exp]) = new ExperimentFromStorageAndStrategyBuilder(storage, strategy)
}

class ExperimentFromStorageAndStrategyBuilder[Storage, Exp](val storage: StorageStrategy[Storage], val experimentStrategy: ExperimentStrategy[Exp]) {
  def apply[Control](control: => Control, candidate: => Exp)(implicit
                                                             controlSerializer: Serialization[Control, Storage],
                                                             candidateSerializer: Serialization[Exp, Storage],
                                                             throwableSerializer: Serialization.ThrowableSerialization[Storage]): Control =
    new Experiment(control, candidate, controlSerializer, candidateSerializer, throwableSerializer, storage, experimentStrategy).run()
}

class Experiment[Control, Exp, Storage](
  control: Control,
  candidate: => Exp,
  controlSerializer: Serialization[Control, Storage],
  candidateSerializer: Serialization[Exp, Storage],
  throwableSerializer: Serialization.ThrowableSerialization[Storage],
  storageStrategy: StorageStrategy[Storage],
  experimentStrategy: ExperimentStrategy[Exp],
  duringExperiment: Option[DuringExperiment[Control, Exp]] = Option.empty,
  afterExperiments: Seq[AfterExperiment] = Seq.empty
) {
  def withDuringExperiment(during: DuringExperiment[Control, Exp]): Experiment[Control, Exp, Storage] =
    new Experiment(control, candidate, controlSerializer, candidateSerializer, throwableSerializer, storageStrategy, experimentStrategy, Some(during), afterExperiments)

  def withAfterExperiment(after: AfterExperiment): Experiment[Control, Exp, Storage] =
    new Experiment(control, candidate, controlSerializer, candidateSerializer, throwableSerializer, storageStrategy, experimentStrategy, duringExperiment, afterExperiments :+ after)

  def run(): Control = {
    try {
      val c = duringExperiment.map { during =>
        during.duringControl(control)
      } getOrElse control

      val controlSerialized = controlSerializer.serialize(c)
      afterExperiments.foreach(_.afterControl())

      val candidateSerialized = {
        val during = duringExperiment getOrElse DuringExperiment.empty
        for {
          candidate <- experimentStrategy.experiment(during.duringExperiment(candidate))
        } yield {
          val result = candidateSerializer.serialize(candidate)
          afterExperiments.foreach(_.afterExperiment())
          result
        }
      }

      storageStrategy.store(controlSerialized, candidateSerialized)
    } catch {
      case NonFatal(err) =>
        storageStrategy.failed(controlSerializer.serialize(control), err)(throwableSerializer)
        afterExperiments.foreach(_.afterExceptionInExperiment())
    }
    control
  }
}

object DuringExperiment {
  def empty[C, E] = new DuringExperiment[C, E] {
    def duringControl(c: => C): C = c
    def duringExperiment(e: => E): E = e
  }
}

trait DuringExperiment[C, E] {
  def duringControl(c: => C): C
  def duringExperiment(e: => E): E
}

trait AfterExperiment {
  def afterControl(): Unit
  def afterExperiment(): Unit
  def afterExceptionInExperiment(): Unit
}

object AfterExperiment {
  lazy val default: AfterExperiment = new AfterExperiment {
    def afterControl(): Unit = {}
    def afterExperiment(): Unit = {}
    def afterExceptionInExperiment(): Unit = {}
  }
}
