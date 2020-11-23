package it.twinsbrains.chapter4

import assertk.assertThat
import assertk.assertions.isEqualTo
import it.twinsbrains.chapter4.Option.Companion.none
import it.twinsbrains.chapter4.Option.Companion.some
import org.junit.Test

class OptionTest {
  @Test
  fun `map should work`() {
    assertThat(
      some(2).map { it * 2 }
    ).isEqualTo(Some(4))

    assertThat(
      none<Int>().map { it * 2 }
    ).isEqualTo(None)
  }

  @Test
  fun `flatMap should work`() {
    assertThat(
      some(2).flatMap { Some(it * 2) }
    ).isEqualTo(Some(4))

    assertThat(
      none<Int>().flatMap { Some(it * 2) }
    ).isEqualTo(None)
  }

  @Test
  fun `getOrElse should work`() {
    assertThat(
      some(2).getOrElse { 4 }
    ).isEqualTo(2)

    assertThat(
      none<Int>().getOrElse { 4 }
    ).isEqualTo(4)
  }
}