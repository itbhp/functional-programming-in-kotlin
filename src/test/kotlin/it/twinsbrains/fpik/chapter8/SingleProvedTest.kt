package it.twinsbrains.fpik.chapter8

import it.twinsbrains.fpik.chapter8.Prop.Companion.check
import it.twinsbrains.fpik.chapter8.Prop.Companion.run
import org.junit.jupiter.api.Test
import strikt.api.expectThat
import strikt.assertions.isA

class SingleProvedTest {

  @Test
  fun `it should prove single properties`() {
    val x = 400
    val isEven = { x % 2 == 0 }
    expectThat(run(check(isEven))).isA<Proved>()
  }
}