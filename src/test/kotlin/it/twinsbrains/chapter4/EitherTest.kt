package it.twinsbrains.chapter4

import assertk.assertThat
import assertk.assertions.isEqualTo
import it.twinsbrains.chapter3.List
import it.twinsbrains.chapter4.Either.Companion.left
import it.twinsbrains.chapter4.Either.Companion.right
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

  @Test
  fun sequenceOnLeft() {
    assertThat(Either.sequence(List.of(right(42), left("Error"))))
      .isEqualTo(left<String, Int>("Error"))
  }

  @Test
  fun sequenceOnRight() {
    assertThat(Either.sequence(List.of(right<String, Int>(42), right(43))))
      .isEqualTo(right(List.of(42, 43)))
  }

  @Test
  fun `lift test`() {
    val convert = { a: Int -> a.toString() }
    assertThat(Either.lift<String, Int, String>(convert)(right(2))).isEqualTo(right("2"))
  }

  @Test
  fun `catches test`() {
    val runtimeException = RuntimeException("boom")
    assertThat(Either.catches { throw runtimeException }).isEqualTo(left(runtimeException))
    assertThat(Either.catches { 1 }).isEqualTo(right(1))
  }
}