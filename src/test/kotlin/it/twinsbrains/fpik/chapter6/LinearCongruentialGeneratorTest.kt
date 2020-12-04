package it.twinsbrains.fpik.chapter6

import org.junit.Assert.assertNotEquals
import org.junit.Test
import it.twinsbrains.fpik.chapter6.LinearCongruentialGenerator as SimpleRNG

class LinearCongruentialGeneratorTest {

  @Test
  fun `should have some randomness`() {
    val (r1, s1) = SimpleRNG(3).nextInt()
    val (r2, s2) = s1.nextInt()
    val (r3, _) = s2.nextInt()

    assertNotEquals(r1, r2)
    assertNotEquals(r2, r3)
    assertNotEquals(r1, r3)
  }
}