package it.twinsbrains.fpik.chapter8

import org.junit.jupiter.api.Test
import strikt.api.expectThat
import strikt.assertions.isA
import it.twinsbrains.fpik.chapter6.LinearCongruentialGenerator as AnRNG

class SizedGenTest {

  @Test
  internal fun map() {
    val sgen = Gen.choose(1, 100).unsized()
    val mapped = sgen.map { it + 200 }
    val result = Checkers.forAll(mapped(1)) { it >= 201 }.check(10, AnRNG(2))
    expectThat(result).isA<Passed>()
  }
}