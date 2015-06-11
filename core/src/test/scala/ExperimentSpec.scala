package org.decaf.science
import org.specs2.specification.Scope
import org.specs2.mutable.Specification

object ExperimentSpec extends Specification {

  "always run the experiment" in pending
  "never run the experiment" in pending

  trait context extends Scope {
    var controlCounter = 0
    var experimentCounter = 1
  }
}
