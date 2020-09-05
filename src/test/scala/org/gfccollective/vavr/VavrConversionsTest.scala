package org.gfccollective.vavr

import io.vavr.control.{Option => VavrOption}

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

}
