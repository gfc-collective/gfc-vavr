package org.gfccollective.vavr

import io.vavr.control.{Option => VavrOption}
import io.vavr.control.{Either => VavrEither}
import io.vavr.control.{Try => VavrTry}

import scala.util.{Try, Success, Failure}

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

  /** Implicit conversion from vavr's <code>Either</code> to
   *  to scala's <code>Either</code>
   */
  implicit def asScalaEither[L, R](vEither: VavrEither[L, R]): Either[L, R] = {
    vEither match {
      case vLeft: VavrEither.Left[L, R] => Left[L, R](vLeft.getLeft)
      case vRight: VavrEither.Right[L, R] => Right[L, R](vRight.get)
    }
  }

  /** Implicit conversion from scala's <code>Either</code> to
   *  to vavr's <code>Either</code>
   */
  implicit def asJavaVavrEither[L, R](sEither: Either[L, R]): VavrEither[L, R] = {
    sEither match {
      case Left(x) => VavrEither.left(x)
      case Right(x) => VavrEither.right(x)
    }
  }

  /** Implicit conversion from vavr's <code>Try</code> to
   *  to scala's <code>Try</code>
   */
  implicit def asScalaTry[T](vTry: VavrTry[T]): Try[T] = {
    vTry match {
      case success: VavrTry.Success[T] => Success[T](success.get)
      case failure: VavrTry.Failure[Throwable] => Failure[T](failure.getCause)
    }
  }

  /** Implicit conversion from scala's <code>Try</code> to
   *  to vavr's <code>Try</code>
   */
  implicit def asJavaVavrTry[T](sTry: Try[T]): VavrTry[T] = {
    sTry match {
      case Success(x) => VavrTry.success(x)
      case Failure(exception) => VavrTry.failure(exception)
    }
  }
}
