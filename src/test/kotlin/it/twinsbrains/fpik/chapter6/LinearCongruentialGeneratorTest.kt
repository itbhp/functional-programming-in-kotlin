package it.twinsbrains.fpik.chapter6

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.Test
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

  @Test
  fun `using the same instance we get the same result no randomness`() {
    val s = SimpleRNG(3)
    val (r1, _) = s.nextInt()
    val (r2, _) = s.nextInt()
    val (r3, _) = s.nextInt()

    assertEquals(r1, r2)
    assertEquals(r2, r3)
    assertEquals(r1, r3)
  }
}