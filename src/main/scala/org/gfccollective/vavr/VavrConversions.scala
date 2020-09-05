package org.gfccollective.vavr

import io.vavr.control.{Option => VavrOption}

/**
 * In spirit of scala.collection.JavaConversions.
 * 
 * Implicit conversion functions for VAVR datatypes.
 */
object VavrConversions {

  import scala.language.implicitConversions

  /** Implicit conversion from vavr's <code>Option</code> to
   *  to scala's <code>Option</code>
   */
  implicit def asScalaOption[T](vOpt: VavrOption[T]): Option[T] = {
    if (vOpt.isDefined) Some(vOpt.get())
    else None
  }

  /** Implicit conversion from scala's <code>Option</code> to
   *  to vavr's <code>Option</code>
   */
  implicit def asJavaVavrOption[T](sOpt: Option[T]): VavrOption[T] = {
    sOpt.map(VavrOption.of[T](_)).getOrElse(VavrOption.none())
  }

}
