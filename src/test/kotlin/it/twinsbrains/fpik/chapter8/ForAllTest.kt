package it.twinsbrains.fpik.chapter8


import it.twinsbrains.fpik.chapter6.LinearCongruentialGenerator
import org.junit.jupiter.api.Test
import strikt.api.expectThat
import strikt.assertions.isEqualTo

class ForAllTest {

  @Test
  fun `it should work`() {
    val propToCheck = Checkers.forAll(Gen.choose(1, 100)) { it < 100 }
    val res = propToCheck.check(100, LinearCongruentialGenerator(2))
    expectThat(res).isEqualTo(Passed)
  }
}