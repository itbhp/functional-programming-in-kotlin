package it.twinsbrains.fpik.chapter8

import arrow.mtl.run
import it.twinsbrains.fpik.chapter6.LinearCongruentialGenerator
import org.junit.jupiter.api.Test
import strikt.api.expectThat
import strikt.assertions.isGreaterThanOrEqualTo
import strikt.assertions.isLessThan

class GenTest {
  @Test
  fun `it should work`() {
    val start = 10
    val stopExclusive = 1000
    val choose = Gen.choose(start, stopExclusive)
    val example = choose.sample.run(LinearCongruentialGenerator(3)).b

    expectThat(example).isGreaterThanOrEqualTo(start)
    expectThat(example).isLessThan(stopExclusive)
  }
}