package org.gfccollective.vavr

import io.vavr.control.{Option => VavrOption}
import io.vavr.control.{Either => VavrEither}
import io.vavr.control.{Try => VavrTry}

import scala.util.{Try, Success, Failure}

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

  implicit class EitherConverter[L, R](val vEither: VavrEither[L, R]) extends AnyVal {
    @inline def asScala: Either[L, R] = if (vEither.isLeft) {
      Left(vEither.getLeft)
    }else {
      Right(vEither.get)
    }
  }

  implicit class VavrEitherConverter[L, R](val either: Either[L, R]) extends AnyVal {
    @inline def asJava: VavrEither[L, R] = either match {
      case Left(leftVal) => VavrEither.left(leftVal)
      case Right(rightVal) => VavrEither.right(rightVal)
    }
  }

  implicit class TryConverter[T](val vTry: VavrTry[T]) extends AnyVal {
    @inline def asScala: Try[T] = vTry match {
      case success: VavrTry.Success[T] => Success(success.get)
      case failure: VavrTry.Failure[T] => Failure(failure.getCause)
    }
  }

  implicit class VavrTryConverter[T](val sTry: Try[T]) extends AnyVal {
    @inline def asJava: VavrTry[T] = sTry match {
      case Success(x) => VavrTry.success(x)
      case Failure(throwable) => VavrTry.failure(throwable)
    }
  }

}
