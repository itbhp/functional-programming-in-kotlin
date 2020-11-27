package it.twinsbrains.chapter4

import assertk.assertThat
import assertk.assertions.isEqualTo
import it.twinsbrains.chapter3.List
import it.twinsbrains.chapter4.Option.Companion.catches
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

  @Test
  fun `orElse should work`() {
    assertThat(
      some(2).orElse { Some(4) }
    ).isEqualTo(Some(2))

    assertThat(
      none<Int>().orElse { Some(4) }
    ).isEqualTo(Some(4))
  }

  @Test
  fun `filter should work`() {
    assertThat(
      some(2).filter { it % 2 != 0 }
    ).isEqualTo(None)

    assertThat(
      some(2).filter { it % 2 == 0 }
    ).isEqualTo(Some(2))

    assertThat(
      none<Int>().filter { it % 2 == 0 }
    ).isEqualTo(None)
  }

  @Test
  fun `lift should work`() {
    val convertToString = { a: Int -> a.toString() }
    val someA = some(4)

    assertThat(Option.lift(convertToString)(someA)).isEqualTo(some("4"))
  }

  @Test
  fun `map2 test`() {
    assertThat(Option.map2(some(2), some(4)) { a, b -> a + b }).isEqualTo(some(6))
  }

  @Test
  fun `sequence on empty list`() {
    assertThat(Option.sequence(List.empty<Option<Int>>())).isEqualTo(some(List.empty()))
  }

  @Test
  fun `sequence on list of some`() {
    assertThat(Option.sequence(List.of(some(1), some(2)))).isEqualTo(some(List.of(1, 2)))
  }

  @Test
  fun `sequence on list of some and none`() {
    assertThat(Option.sequence(List.of(some(1), none()))).isEqualTo(none())
  }

  @Test
  fun `catches test`() {
    assertThat(catches { throw Exception() }).isEqualTo(none())
    assertThat(catches { 1 }).isEqualTo(some(1))
  }
}