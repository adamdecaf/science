package org.decaf.science.fs
import java.io.{File, FileWriter, IOException}
import java.nio.file.{Files, StandardOpenOption}
import org.decaf.science.{Serialization, StorageStrategy}
import org.slf4j.LoggerFactory

class FilesystemStorageStrategy(base: File) extends StorageStrategy[Array[Byte]] {
  def store(control: Array[Byte], maybeExperiment: Option[Array[Byte]] = None): Unit = {
    val now = getNanosAsString()

    val controlFile = new File(s"${basePath}/${now}-control")
    saveToFile(control, controlFile)

    maybeExperiment foreach { experiment =>
      val experimentFile = new File(s"${basePath}/${now}-experiment")
      saveToFile(experiment, experimentFile)
    }
  }

  def failed[T <: Throwable](control: Array[Byte], experiment: T)(implicit serializer: Serialization[T, Array[Byte]]): Unit = {
    val now = getNanosAsString()

    val controlFile = new File(s"${basePath}/${now}-control")
    saveToFile(control, controlFile)

    val failedFile = new File(s"${basePath}/${now}-failed")
    saveToFile(serializer.serialize(experiment), failedFile)
  }

  private[this] def saveToFile(bytes: Array[Byte], file: File): Unit =
    try {
      Files.write(file.toPath, bytes, StandardOpenOption.CREATE, StandardOpenOption.WRITE)
    } catch {
      case err: IOException =>
        log.error(s"Error occurred when trying to safe file at ${file.getAbsolutePath}", err)
    }

  private[this] lazy val basePath = {
    val absolutePath = base.getAbsolutePath
    if (absolutePath.endsWith("/")) {
      absolutePath
    } else {
      base.getParent
    }
  }

  private[this] lazy val log = LoggerFactory.getLogger(this.getClass)
  private[this] def getNanosAsString(): String = System.currentTimeMillis().toString
}
