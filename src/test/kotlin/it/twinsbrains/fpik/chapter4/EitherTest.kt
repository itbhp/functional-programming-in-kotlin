package it.twinsbrains.fpik.chapter4

import it.twinsbrains.fpik.chapter3.List
import it.twinsbrains.fpik.chapter4.Either.Companion.left
import it.twinsbrains.fpik.chapter4.Either.Companion.right
import org.junit.Test
import strikt.api.expectThat
import strikt.assertions.isEqualTo

class EitherTest {

  @Test
  fun `map on Right`() {
    expectThat(Right(2).map { it * 3 }).isEqualTo(Right(6))
  }

  @Test
  fun `map on Left`() {
    expectThat(Left("42").map { it }).isEqualTo(Left("42"))
  }

  @Test
  fun orElse() {
    expectThat(Left("Error").orElse { Right(42) }).isEqualTo(Right(42))
  }

  @Test
  fun sequenceOnLeft() {
    expectThat(Either.sequence(List.of(right(42), left("Error"))))
      .isEqualTo(left("Error"))
  }

  @Test
  fun sequenceOnRight() {
    expectThat(Either.sequence(List.of(right<String, Int>(42), right(43))))
      .isEqualTo(right(List.of(42, 43)))
  }

  @Test
  fun `lift test`() {
    val convert = { a: Int -> a.toString() }
    expectThat(Either.lift<String, Int, String>(convert)(right(2))).isEqualTo(right("2"))
  }

  @Test
  fun `catches test`() {
    val runtimeException = RuntimeException("boom")
    expectThat(Either.catches { throw runtimeException }).isEqualTo(left(runtimeException))
    expectThat(Either.catches { 1 }).isEqualTo(right(1))
  }
}