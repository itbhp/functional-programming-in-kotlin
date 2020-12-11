package it.twinsbrains.fpik.chapter8

import arrow.mtl.run
import it.twinsbrains.fpik.chapter6.LinearCongruentialGenerator
import org.junit.jupiter.api.Test
import strikt.api.expectThat
import strikt.assertions.*

class GenTest {
  @Test
  fun `choose should work`() {
    val start = 10
    val stopExclusive = 1000
    val choose = Gen.choose(start, stopExclusive)
    val example = choose.sample.run(LinearCongruentialGenerator(3)).b

    expectThat(example).isGreaterThanOrEqualTo(start)
    expectThat(example).isLessThan(stopExclusive)
  }

  @Test
  fun `unit should work`() {
    val string = Gen.unit("4")
    val stringExample = string.sample.run(LinearCongruentialGenerator(3)).b
    expectThat(stringExample).isEqualTo("4")

    val int = Gen.unit(4)
    val intExample = int.sample.run(LinearCongruentialGenerator(3)).b
    expectThat(intExample).isEqualTo(4)
  }

  @Test
  fun `boolean should work`() {
    val gen = Gen.boolean()
    val aVal = gen.sample.run(LinearCongruentialGenerator(3)).b
    expectThat(aVal).isA<Boolean>()
  }

  @Test
  fun `listOfN should work`() {
    val gen = Gen.listOfN(10, Gen.choose(1, 100))
    val aList = gen.sample.run(LinearCongruentialGenerator(3)).b
    expectThat(aList).isA<List<Int>>()
    expectThat(aList).hasSize(10)
    expectThat(aList).all { isGreaterThanOrEqualTo(1) and { isLessThan(100) } }
  }
}