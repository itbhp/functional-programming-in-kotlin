package it.twinsbrains.chapter4

import assertk.assertThat
import assertk.assertions.isEqualTo
import org.junit.Test

class EitherTest {

  @Test
  fun `map on Right`() {
    assertThat(Right(2).map { it * 3 }).isEqualTo(Right(6))
  }

  @Test
  fun `map on Left`() {
    assertThat(Left("42").map { it }).isEqualTo(Left("42"))
  }

  @Test
  fun orElse() {
    assertThat(Left("Error").orElse { Right(42) }).isEqualTo(Right(42))
  }
}