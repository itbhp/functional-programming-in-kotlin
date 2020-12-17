package it.twinsbrains.fpik.chapter8

import it.twinsbrains.fpik.chapter8.Checkers.forAll
import it.twinsbrains.fpik.chapter8.Gen.Companion.choose
import org.junit.jupiter.api.Test
import strikt.api.expectThat
import strikt.assertions.isA
import strikt.assertions.isEqualTo

class ForAllTest {

  @Test
  fun `it should work`() {
    val propToCheck = forAll(choose(1, 100)) { it < 100 }
    val res = propToCheck.verify()
    expectThat(res).isEqualTo(Passed)
  }

  @Test
  fun `it should also fail`() {
    val propToCheck = forAll(choose(1, 100)) { it > 100 }
    val res = propToCheck.verify()
    expectThat(res).isA<Falsified>()
  }

  @Test
  fun `and should work`() {
    val gen = choose(1, 100)
    val propToCheck = forAll(gen) { it < 100 }.and(forAll(gen) { it >= 1 })
    val res = propToCheck.verify()
    expectThat(res).isA<Passed>()
  }

  @Test
  fun `or should work`() {
    val gen = choose(1, 100)
    val propToCheck = forAll(gen) { it < 100 }.or(forAll(gen) { it > 1 })
    val res = propToCheck.verify()
    expectThat(res).isA<Passed>()
  }

  private fun Prop.verify() = Prop.run(this)
}