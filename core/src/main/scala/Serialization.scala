package org.adamdecaf.scientist

trait Serialization[From, To] {
  def serialize(from: From): To
}

object Serialization {
  implicit def ThrowableToStringSerialization[To](f: String => To): Serialization[Throwable, To] = new Serialization[Throwable, To] {
    def serialize(from: Throwable): To = f(from.getStackTrace.mkString("\n"))
  }
}
