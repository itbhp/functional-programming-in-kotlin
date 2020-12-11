package it.twinsbrains.fpik.chapter8

import arrow.mtl.run
import it.twinsbrains.fpik.chapter6.LinearCongruentialGenerator
import it.twinsbrains.fpik.chapter6.RNG
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

  @Test
  fun `listOfN with gen should work`() {
    val gen = Gen.listOfN(Gen.unit(10), Gen.choose(1, 100))
    val aList = gen.sample.run(LinearCongruentialGenerator(3)).b
    expectThat(aList).isA<List<Int>>()
    expectThat(aList).hasSize(10)
    expectThat(aList).all { isGreaterThanOrEqualTo(1) and { isLessThan(100) } }
  }

  @Test
  fun `union should work`() {
    val union = Gen.union(Gen.unit(2), Gen.unit(4))
    val rng: RNG = LinearCongruentialGenerator(2)
    val (_, genValues) = (1..1000).fold(Pair(rng, listOf<Int>())) { acc, _ ->
      val (iRng, l) = acc
      val (nRng, aVal) = union.sample.run(iRng)
      nRng to (l + aVal)
    }

    expectThat(genValues).filter { it == 2 }.get { size }.isIn(480..520)
    expectThat(genValues).filter { it == 4 }.get { size }.isIn(480..520)
  }

  @Test
  fun `weighted should work`() {
    val union = Gen.weighted(Pair(Gen.unit(2), 0.2), Pair(Gen.unit(4), 0.8))
    val rng: RNG = LinearCongruentialGenerator(2)
    val (_, genValues) = (1..1000).fold(Pair(rng, listOf<Int>())) { acc, _ ->
      val (iRng, l) = acc
      val (nRng, aVal) = union.sample.run(iRng)
      nRng to (l + aVal)
    }

    expectThat(genValues).filter { it == 2 }.get { size }.isIn(180..220)
    expectThat(genValues).filter { it == 4 }.get { size }.isIn(780..820)
  }
}