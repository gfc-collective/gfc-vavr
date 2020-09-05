package org.gfccollective.vavr

import io.vavr.control.{Option => VavrOption}

/**
 * In spirit of scala.collection.JavaConverters.
 * 
 * Implicit conversion functions for VAVR datatypes.
 */
object VavrConverters {

  implicit class OptionConverter[T](val vOpt: VavrOption[T]) extends AnyVal {
    @inline def asScala: Option[T] = if (vOpt.isDefined) {
      Option(vOpt.get())
    }else {
      None
    }
  }
  implicit class VavrOptionConverter[T](val option: Option[T]) extends AnyVal {
    @inline def asJava: VavrOption[T] = option.map(VavrOption.of[T](_)).getOrElse(VavrOption.none[T]())
  }

}
