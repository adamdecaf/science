package org.decaf.science
import scala.language.implicitConversions

trait Serialization[From, To] {
  def serialize(from: From): To
}

object Serialization {
  type ThrowableSerialization[To] = Serialization[Throwable, To]

  implicit val ToStringThrowableSerializer: ThrowableSerialization[String] = new Serialization[Throwable, String] {
    def serialize(from: Throwable): String = from.getStackTrace.mkString("\n")
  }
}
