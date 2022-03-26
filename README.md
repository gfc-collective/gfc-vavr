# gfc-vavr [![Maven Central](https://maven-badges.herokuapp.com/maven-central/org.gfccollective/gfc-vavr_2.12/badge.svg?style=plastic)](https://maven-badges.herokuapp.com/maven-central/org.gfccollective/gfc-vavr_2.12) [![Build Status](https://github.com/gfc-collective/gfc-vavr/workflows/Scala%20CI/badge.svg)](https://github.com/gfc-collective/gfc-vavr/actions) [![Coverage Status](https://coveralls.io/repos/gfc-collective/gfc-vavr/badge.svg?branch=main&service=github)](https://coveralls.io/github/gfc-collective/gfc-vavr?branch=main)


A library that contains utility classes and Scala adapters and adaptations for [VAVR](https://www.vavr.io/).


## Getting gfc-vavr

This library is cross-built against Scala 2.12.x, 2.13.x, and Scala 3.0.x

If you're using SBT, add the following line to your build file:

```scala
libraryDependencies += "org.gfccollective" %% "gfc-vavr" % "x.y.z"
```

For Maven and other build tools, you can visit [search.maven.org](http://search.maven.org/#search%7Cga%7C1%7Corg.gfccollective).
(This search will also list other available libraries from the GFC Collective.)

## Contents and Example Usage

### org.gfccollective.vavr.VavrConverters / VavrConversions:
These contain implicit and explicit functions to convert between
* ```io.vavr.control.Option``` and ```scala.Option```
* ```io.vavr.control.Either``` and ```scala.util.Either```
* ```io.vavr.control.Try``` and ```scala.util.Try```
* ```io.vavr.concurrent.Future``` and ```scala.concurrent.Future```

```scala
    val foo: Option[String] = ???

    // Explicit conversion Scala Option -> Vavr Option
    import org.gfccollective.vavr.VavrConverters._
    val bar: io.vavr.control.Option[String] = foo.asVavrOption

    // Implicit conversion Vavr Option -> Scala Option
    import org.gfccollective.vavr.VavrConversions._
    val baz: Option[String] = bar
```


## License

Licensed under the Apache License, Version 2.0: http://www.apache.org/licenses/LICENSE-2.0
