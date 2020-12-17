package it.twinsbrains.fpik.chapter8

import it.twinsbrains.fpik.chapter8.Checkers.forAll
import org.junit.jupiter.api.Test
import strikt.api.expectThat
import strikt.assertions.isA
import it.twinsbrains.fpik.chapter6.LinearCongruentialGenerator as AnRNG

class SizedGenTest {

  @Test
  fun map() {
    val sgen = Gen.choose(1, 100).unsized()
    val mapped = sgen.map { it + 200 }
    val result = forAll(mapped(1)) { it >= 201 }.verify()
    expectThat(result).isA<Passed>()
  }

  @Test
  fun flatMap() {
    val sgen = Gen.choose(1, 100).unsized()
    val mapped = sgen.flatMap { Gen.unit(it) }
    val result = forAll(mapped(1)) { it < 100 }.verify()
    expectThat(result).isA<Passed>()
  }

  @Test
  fun `listOf should work`() {
    val sgen = Gen.choose(1, 100).listOf()
    val ga = sgen(10)
    val res = forAll(ga) { it.size == 10 }.verify()
    expectThat(res).isA<Passed>()
  }

  @Test
  fun `SGEN forAll should work`() {
    val sgen = Gen.choose(1, 100).listOf()
    val res = forAll(sgen) { it.isEmpty() || it.maxOrNull()!! < 100 }.verify()
    expectThat(res).isA<Passed>()
  }

  private fun Prop.verify() = this.check(100, 10, AnRNG(2))
}