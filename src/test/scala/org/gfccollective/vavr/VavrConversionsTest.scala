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

  test("Converters Scala Option asVavrOption") {
    import VavrConverters._

    val optAbsent: VavrOption[String] = Option.empty[String].asVavrOption
    optAbsent should be(VavrOption.none())

    val optFoo: VavrOption[String] = Some("foo").asVavrOption
    optFoo should be(VavrOption.of("foo"))
    None.asVavrOption should be(VavrOption.none())

  }

  test("Conversions Vavr Option asScala") {
    import VavrConversions._
    val optNone: Option[String] = VavrOption.none[String]()
    optNone should be(None)

    val optFoo: Option[String] = VavrOption.of("foo")
    optFoo should be(Some("foo"))
  }

  test("Conversions Scala Option to VAVR Option") {
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

  test("Converters Scala Either asVavrEither") {
    import VavrConverters._

    val left: VavrEither[String, String] = Left("left123").asVavrEither
    left should be(VavrEither.left("left123"))

    val right: VavrEither[String, String] = Right("right123").asVavrEither
    right should be(VavrEither.right("right123"))

  }

  test("Conversions Vavr Either asScala") {
    import VavrConversions._

    val left: Either[String, String] = VavrEither.left[String, String]("left123")
    left should be(Left("left123"))

    val right: Either[String, String] = VavrEither.right[String, String]("right123")
    right should be(Right("right123"))
  }

  test("Conversions Scala Either to VAVR Either") {
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

  test("Converters Scala Try asVavrTry") {
    import VavrConverters._

    val success: VavrTry[String] = Success("success123").asVavrTry
    success.get should be("success123")

    val exception = new IllegalStateException("foobar")
    val failure: VavrTry[String] = Failure(exception).asVavrTry
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

  test("Conversions Scala Try to VAVR Try") {
    import VavrConversions._

    val success: VavrTry[String] = Success("success123")
    success should be(VavrTry.success("success123"))

    val exception = new IllegalStateException("foobar")
    val failure: VavrTry[String] = Failure(exception)
    failure should be(VavrTry.failure(exception))
  }
}
