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

  object Strings {
    implicit def StringSerializer[T]: Serialization[T, String] = new Serialization[T, String] {
      def serialize(from: T): String = from.toString
    }
  }

  implicit def IdentitySerializer[T]: Serialization[T, T] = new Serialization[T, T] {
    def serialize(from: T): T = from
  }
}
