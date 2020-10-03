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

  private val defaultSleepTime = Duration(200, MILLISECONDS)

  implicit override val patienceConfig = PatienceConfig(
                                             timeout = scaled(Span(defaultSleepTime.toMillis * 5, Millis)),
                                             interval = scaled(Span(5, Millis)))

  test("Vavr Future asScala") {
    import FutureConverters._

    val vFuture = makeVavrFuture()
    vFuture.isAsync should be(true)
    vFuture.isCancelled should be(false)
    vFuture.isCompleted should be(false)

    val sFuture: Future[String] = vFuture.asScala

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
      vFuture.isCancelled should be(false)
      vFuture.isCompleted should be(true)
    }
  }

  test("Scala Future asVavrFuture") {
    import FutureConverters._

    val vFuture: VavrFuture[String] = makeScalaFuture().asVavrFuture
    vFuture.isAsync should be(true)
    vFuture.isCompleted should be(false)

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

  test("roundtrip with failed Scala Future") {
    import FutureConverters._

    val vFuture: VavrFuture[String] = makeFailedScalaFuture().asVavrFuture

    eventually {
      vFuture.isCompleted should be(true)
      vFuture.isSuccess should be(false)
      vFuture.isFailure should be(true)
      vFuture.isCancelled should be(false)
      vFuture.failed().get.getMessage should be("This is a failed Scala Future")

      val sFuture = vFuture.asScala
      sFuture.isCompleted should be(true)
      sFuture.isExpired should be(false)
      sFuture.isCanceled should be(false)
      sFuture.failed.futureValue.getMessage should be("This is a failed Scala Future")
    }
  }

  test("roundtrip with failed VAVR Future") {
    import FutureConverters._

    val sFuture: Future[String] = makeFailedVavrFuture().asScala

    eventually {
      sFuture.isCompleted should be(true)
      sFuture.isExpired should be(false)
      sFuture.isCanceled should be(false)
      sFuture.failed.futureValue.getMessage should be("This is a failed VAVR Future")

      val vFuture: VavrFuture[String] = sFuture.asVavrFuture
      vFuture.isCompleted should be(true)
      vFuture.isSuccess should be(false)
      vFuture.isFailure should be(true)
      vFuture.isCancelled should be(false)
      vFuture.failed().get.getMessage should be("This is a failed VAVR Future")
    }
  }

  test("Scala for comprehension: 3 futures") {
    import FutureConverters._

    val future1 = makeScalaFuture("1")
    val future2 = makeVavrFuture("2").asScala
    val future3 = makeScalaFuture("3").asVavrFuture.asScala

    val result = for {
      x <- future1
      y <- future2
      z <- future3
    } yield {
      x + "," + y + "," + z
    }

    result.futureValue should be("1,2,3")
  }

  test("Scala for comprehension: 2 futures") {
    import FutureConverters._

    val startTime = System.currentTimeMillis

    val futureSleepTime = Duration(300, MILLISECONDS)

    val sFuture1: Future[String] = makeVavrFuture("message1", futureSleepTime).asScala
    val sFuture2: Future[String] = makeVavrFuture("message2", futureSleepTime).asScala

    val result: Future[String] = for {
      message1 <- sFuture1
      message2 <- sFuture2
    } yield {
      message1 + "," + message2
    }

    result.futureValue should be("message1,message2")

    val endTime = System.currentTimeMillis

    (endTime - startTime) shouldBe > (futureSleepTime.toMillis)
  }

  test("VAVR For statement") {
    import FutureConverters._
    import io.vavr.API.For

    val startTime = System.currentTimeMillis

    val futureSleepTime = Duration(300, MILLISECONDS)

    val vFuture1: VavrFuture[String] = makeScalaFuture("message1", futureSleepTime).asVavrFuture
    val vFuture2: VavrFuture[String] = makeScalaFuture("message2", futureSleepTime).asVavrFuture

    val result = For(vFuture1, vFuture2)
      .`yield`((a, b) => a + "," + b)

    result.get should be("message1,message2")

    val endTime = System.currentTimeMillis

    (endTime - startTime) shouldBe > (futureSleepTime.toMillis)

  }

  private def makeScalaFuture(message: String = "bonjour", sleepTime: Duration = defaultSleepTime): Future[String] = {
    Future {
      Thread.sleep(sleepTime.toMillis)
      message
    }
  }

  private def makeFailedScalaFuture(sleepTime: Duration = defaultSleepTime): Future[String] = {
    Future {
      Thread.sleep(sleepTime.toMillis)
      throw new IllegalStateException("This is a failed Scala Future")
    }
  }

  private def makeVavrFuture(message: String = "bonjour", sleepTime: Duration = defaultSleepTime): VavrFuture[String] = {
    // the Scala compiler was having trouble invoking
    // VavrFuture.of because 'of' is an overloaded method.
    // My workaround is to explicitly create a CheckedFunction0 object.
    val computation = new CheckedFunction0[String]() {
      override def apply(): String = {
        Thread.sleep(sleepTime.toMillis)
        message
      }
    }
    VavrFuture.of(computation)
  }

  private def makeFailedVavrFuture(sleepTime: Duration = defaultSleepTime): VavrFuture[String] = {
    // the Scala compiler was having trouble invoking
    // VavrFuture.of because 'of' is an overloaded method.
    // My workaround is to explicitly create a CheckedFunction0 object.
    val computation = new CheckedFunction0[String]() {
      override def apply(): String = {
        Thread.sleep(sleepTime.toMillis)
        throw new IllegalStateException("This is a failed VAVR Future")
      }
    }
    VavrFuture.of(computation)
  }
}
