package org.gfccollective.vavr.future

import java.util.concurrent.TimeUnit
import java.util.function.Consumer
import java.util.function.{Function => JFunction}

import io.vavr.concurrent.{Future => VavrFuture}
import io.vavr.control.{Try => VavrTry}
import org.gfccollective.vavr.VavrConverters.VavrTryConverter

import scala.concurrent.{CanAwait, ExecutionContext, Future}
import scala.concurrent.duration.Duration
import scala.util.Try

/**
 * In spirit of scala.collection.JavaConverters.
 *
 * Implicit conversion functions for VAVR Future objects.
 */
object FutureConverters {

  implicit class FutureConverter[T](val vFuture: VavrFuture[T]) extends AnyVal {
    @inline def asScala: Future[T] = {
      vFuture match {
        case adapter: ScalaFutureAdapter[T] => adapter.getScalaFuture
        case _ => new VavrFutureAdapter[T](vFuture)
      }
    }
  }

  implicit class VavrFutureConverter[T](val sFuture: Future[T]) extends AnyVal {
    @inline def asVavrFuture: VavrFuture[T] = new ScalaFutureAdapter(sFuture)
  }

  def registerOnComplete[T, U >: VavrTry[T]](scalaFuture: Future[T], action: Consumer[U]): Unit = {
    import scala.concurrent.ExecutionContext.Implicits.global

    scalaFuture.onComplete(result => {
      action.accept(result.asVavrTry)
    })
  }

  case class VavrFutureAdapter[T](vavrFuture: VavrFuture[T]) extends Future[T] {

    override def onComplete[U](f: Try[T] => U)(implicit executor: ExecutionContext): Unit = {
      vavrFuture.onComplete(new Consumer[VavrTry[T]]() {
        override def accept(result: VavrTry[T]): Unit = {
          import org.gfccollective.vavr.VavrConverters._
          f.apply(result.asScala)
        }
      })
    }

    override def isCompleted: Boolean = vavrFuture.isCompleted

    override def value: Option[Try[T]] = {
      import org.gfccollective.vavr.VavrConverters._
      vavrFuture.getValue.asScala.map(_.asScala)
    }

    override def transform[S](f: Try[T] => Try[S])(implicit executor: ExecutionContext): Future[S] = {
      import org.gfccollective.vavr.VavrConverters._
      val jFunction = new JFunction[VavrTry[T], VavrTry[S]]() {
        override def apply(vavrTry: VavrTry[T]): VavrTry[S] = {
          f(vavrTry.asScala).asVavrTry
        }
      }
      vavrFuture.transformValue(jFunction).asScala
    }

    override def transformWith[S](f: Try[T] => Future[S])(implicit executor: ExecutionContext): Future[S] = {
      import org.gfccollective.vavr.VavrConverters._
      val jFunction = new JFunction[VavrTry[T], VavrTry[S]]() {
        override def apply(vavrTry: VavrTry[T]): VavrTry[S] = {
          f(vavrTry.asScala).asVavrFuture.toTry
        }
      }
      vavrFuture.transformValue(jFunction).asScala
    }

    override def ready(atMost: Duration)(implicit permit: CanAwait): VavrFutureAdapter.this.type = {
      vavrFuture.await(atMost.toMillis, TimeUnit.MILLISECONDS)
      this
    }

    override def result(atMost: Duration)(implicit permit: CanAwait): T = {
      vavrFuture.await(atMost.toMillis, TimeUnit.MILLISECONDS).get
    }
  }
}
