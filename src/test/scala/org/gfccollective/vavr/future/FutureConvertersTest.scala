package org.gfccollective.vavr.future

import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger
import java.util.function.Consumer

import io.vavr.CheckedFunction0
import io.vavr.concurrent.{Future => VavrFuture}
import io.vavr.control.{Try => VavrTry}

import scala.concurrent.{Await, Future}
import scala.util.Success
import org.scalatest.concurrent.Eventually
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers
import org.scalatest.time.{Millis, Seconds, Span}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.{Duration, MILLISECONDS}

class FutureConvertersTest
  extends AnyFunSuite
  with Matchers
  with Eventually
  with ScalaFutures {

  implicit override val patienceConfig = PatienceConfig(
                                             timeout = scaled(Span(1, Seconds)),
                                             interval = scaled(Span(5, Millis)))

  test("Vavr Future asScala") {
    import FutureConverters._

    val sFuture: Future[String] = makeVavrFuture().asScala

    val counter = new AtomicInteger(0)

    sFuture.onComplete { result =>
      counter.incrementAndGet()
    }

    eventually {
      counter.intValue should be(1)
      sFuture.isCompleted should be(true)
      sFuture.futureValue should be("bonjour")
      sFuture.value should be(Some(Success("bonjour")))
      Await.result(sFuture, Duration(5, MILLISECONDS)) should be("bonjour")
    }
  }

  test("Scala Future asVavrFuture") {
    import FutureConverters._

    val vFuture: VavrFuture[String] = makeScalaFuture().asVavrFuture

    val counter = new AtomicInteger(0)

    vFuture.onComplete(new Consumer[VavrTry[String]]() {
      override def accept(t: VavrTry[String]): Unit = {
        counter.incrementAndGet()
      }
    })

    eventually {
      counter.intValue should be(1)
      vFuture.isCompleted should be(true)
      vFuture.isSuccess should be(true)
      vFuture.isFailure should be(false)
      vFuture.get should be("bonjour")
      vFuture.await.get should be("bonjour")
      vFuture.await(2, TimeUnit.SECONDS).get should be("bonjour")
      vFuture.getOrElse("zzz") should be("bonjour")
      vFuture.getOrNull should be("bonjour")
    }
  }

  test("roundtrip") {
    import FutureConverters._

    val vFuture: VavrFuture[String] = makeScalaFuture().asVavrFuture

    eventually {
      vFuture.isCompleted should be(true)
      vFuture.isSuccess should be(true)
      vFuture.isFailure should be(false)
      vFuture.get should be("bonjour")

      val sFuture = vFuture.asScala
      sFuture.isCompleted should be(true)
      sFuture.futureValue should be("bonjour")
      Await.result(sFuture, Duration(5, MILLISECONDS)) should be("bonjour")
    }
  }

  test("Scala for comprehension") {
    import FutureConverters._

    val sFuture1: Future[String] = makeVavrFuture("message1").asScala
    val sFuture2: Future[String] = makeVavrFuture("message2").asScala

    val result = for {
      message1 <- sFuture1
      message2 <- sFuture2
    } yield {
      message1 + "," + message2
    }

    result should be("message1,message2")
  }

  private def makeScalaFuture(message: String = "bonjour"): Future[String] = {
    Future {
      Thread.sleep(200L)
      message
    }
  }

  private def makeVavrFuture(message: String = "bonjour"): VavrFuture[String] = {
    // the Scala compiler was having trouble invoking
    // VavrFuture.of because 'of' is an overloaded method.
    // My workaround is to explicitly create a CheckedFunction0 object.
    val computation = new CheckedFunction0[String]() {
      override def apply(): String = {
        Thread.sleep(200L)
        message
      }
    }
    VavrFuture.of(computation)
  }
}
