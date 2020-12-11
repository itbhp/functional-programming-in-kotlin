package it.twinsbrains.fpik.chapter8

import arrow.mtl.run
import it.twinsbrains.fpik.chapter6.LinearCongruentialGenerator
import org.junit.jupiter.api.Test
import strikt.api.expectThat
import strikt.assertions.isEqualTo
import strikt.assertions.isGreaterThanOrEqualTo
import strikt.assertions.isLessThan

class GenTest {
  @Test
  fun `choose it should work`() {
    val start = 10
    val stopExclusive = 1000
    val choose = Gen.choose(start, stopExclusive)
    val example = choose.sample.run(LinearCongruentialGenerator(3)).b

    expectThat(example).isGreaterThanOrEqualTo(start)
    expectThat(example).isLessThan(stopExclusive)
  }

  @Test
  fun `unit it should work`() {
    val string = Gen.unit("4")
    val stringExample = string.sample.run(LinearCongruentialGenerator(3)).b
    expectThat(stringExample).isEqualTo("4")

    val int = Gen.unit(4)
    val intExample = int.sample.run(LinearCongruentialGenerator(3)).b
    expectThat(intExample).isEqualTo(4)
  }
}