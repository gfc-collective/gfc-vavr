package org.gfccollective.vavr

import io.vavr.control.{Option => VavrOption}
import io.vavr.control.{Either => VavrEither}
import io.vavr.control.{Try => VavrTry}

import scala.util.{Try, Success, Failure}
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers

/**
 * Unit tests for VavrConverters/VavrConversions.
 */
class VavrConversionsTest extends AnyFunSuite with Matchers {

  test("Converters Vavr Option asScala") {
    import VavrConverters._

    val optNone: Option[String] = VavrOption.none[String]().asScala
    optNone should be(None)

    val optFoo: Option[String] = VavrOption.of("foo").asScala
    optFoo should be(Some("foo"))

    None should be(VavrOption.none().asScala)
  }

  test("Converters Scala Option asJava") {
    import VavrConverters._

    val optAbsent: VavrOption[String] = Option.empty[String].asJava
    optAbsent should be(VavrOption.none())

    val optFoo: VavrOption[String] = Some("foo").asJava
    optFoo should be(VavrOption.of("foo"))
    None.asJava should be(VavrOption.none())

  }

  test("Conversions Vavr Option asScala") {
    import VavrConversions._
    val optNone: Option[String] = VavrOption.none[String]()
    optNone should be(None)

    val optFoo: Option[String] = VavrOption.of("foo")
    optFoo should be(Some("foo"))
  }

  test("Conversions Scala Option asJava") {
    import VavrConversions._

    val optAbsent: VavrOption[String] = Option.empty[String]
    optAbsent should be(VavrOption.none())

    val optFoo: VavrOption[String] = Option("foo")
    optFoo should be(VavrOption.of("foo"))
  }

  test("Converters Vavr Either asScala") {
    import VavrConverters._

    val left: Either[String, String] = VavrEither.left("left123").asScala
    left should be(Left("left123"))

    val right: Either[String, String] = VavrEither.right("right123").asScala
    right should be(Right("right123"))

  }

  test("Converters Scala Either asJava") {
    import VavrConverters._

    val left: VavrEither[String, String] = Left("left123").asJava
    left should be(VavrEither.left("left123"))

    val right: VavrEither[String, String] = Right("right123").asJava
    right should be(VavrEither.right("right123"))

  }

  test("Conversions Vavr Either asScala") {
    import VavrConversions._

    val left: Either[String, String] = VavrEither.left[String, String]("left123")
    left should be(Left("left123"))

    val right: Either[String, String] = VavrEither.right[String, String]("right123")
    right should be(Right("right123"))
  }

  test("Conversions Scala Either asJava") {
    import VavrConversions._

    val left: VavrEither[String, String] = Left("left123")
    left should be(VavrEither.left("left123"))

    val right: VavrEither[String, String] = Right("right123")
    right should be(VavrEither.right("right123"))
  }

  // Try

  test("Converters Vavr Try asScala") {
    import VavrConverters._

    val success: Try[String] = VavrTry.success[String]("happy path").asScala
    success should be(Success("happy path"))

    val exception = new IllegalStateException("foobar")
    val failure: Try[String] = VavrTry.failure[String](exception).asScala
    failure should be(Failure(exception))

  }

  test("Converters Scala Try asJava") {
    import VavrConverters._

    val success: VavrTry[String] = Success("success123").asJava
    success.get should be("success123")

    val exception = new IllegalStateException("foobar")
    val failure: VavrTry[String] = Failure(exception).asJava
    failure.getCause should be(exception)

  }

  test("Conversions Vavr Try asScala") {
    import VavrConversions._

    val sucdess: Try[String] = VavrTry.success[String]("success123")
    sucdess should be(Success("success123"))

    val exception = new IllegalStateException("foobar")
    val failure: Try[String] = VavrTry.failure[String](exception)
    failure should be(Failure(exception))
  }

  test("Conversions Scala Try asJava") {
    import VavrConversions._

    val success: VavrTry[String] = Success("success123")
    success should be(VavrTry.success("success123"))

    val exception = new IllegalStateException("foobar")
    val failure: VavrTry[String] = Failure(exception)
    failure should be(VavrTry.failure(exception))
  }
}
