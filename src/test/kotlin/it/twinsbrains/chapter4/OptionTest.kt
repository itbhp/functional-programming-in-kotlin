package it.twinsbrains.chapter4

import it.twinsbrains.chapter3.List
import it.twinsbrains.chapter4.Option.Companion.catches
import it.twinsbrains.chapter4.Option.Companion.none
import it.twinsbrains.chapter4.Option.Companion.some
import org.junit.Test
import strikt.api.expectThat
import strikt.assertions.isEqualTo

class OptionTest {
  @Test
  fun `map should work`() {
    expectThat(
      some(2).map { it * 2 }
    ).isEqualTo(Some(4))

    expectThat(
      none<Int>().map { it * 2 }
    ).isEqualTo(None)
  }

  @Test
  fun `flatMap should work`() {
    expectThat(
      some(2).flatMap { Some(it * 2) }
    ).isEqualTo(Some(4))

    expectThat(
      none<Int>().flatMap { Some(it * 2) }
    ).isEqualTo(None)
  }

  @Test
  fun `getOrElse should work`() {
    expectThat(
      some(2).getOrElse { 4 }
    ).isEqualTo(2)

    expectThat(
      none<Int>().getOrElse { 4 }
    ).isEqualTo(4)
  }

  @Test
  fun `orElse should work`() {
    expectThat(
      some(2).orElse { Some(4) }
    ).isEqualTo(Some(2))

    expectThat(
      none<Int>().orElse { Some(4) }
    ).isEqualTo(Some(4))
  }

  @Test
  fun `filter should work`() {
    expectThat(
      some(2).filter { it % 2 != 0 }
    ).isEqualTo(None)

    expectThat(
      some(2).filter { it % 2 == 0 }
    ).isEqualTo(Some(2))

    expectThat(
      none<Int>().filter { it % 2 == 0 }
    ).isEqualTo(None)
  }

  @Test
  fun `lift should work`() {
    val convertToString = { a: Int -> a.toString() }
    val someA = some(4)

    expectThat(Option.lift(convertToString)(someA)).isEqualTo(some("4"))
  }

  @Test
  fun `map2 test`() {
    expectThat(Option.map2(some(2), some(4)) { a, b -> a + b }).isEqualTo(some(6))
  }

  @Test
  fun `sequence on empty list`() {
    expectThat(Option.sequence(List.empty<Option<Int>>())).isEqualTo(some(List.empty()))
  }

  @Test
  fun `sequence on list of some`() {
    expectThat(Option.sequence(List.of(some(1), some(2)))).isEqualTo(some(List.of(1, 2)))
  }

  @Test
  fun `sequence on list of some and none`() {
    expectThat(Option.sequence(List.of(some(1), none()))).isEqualTo(none())
  }

  @Test
  fun `catches test`() {
    expectThat(catches { throw Exception() }).isEqualTo(none())
    expectThat(catches { 1 }).isEqualTo(some(1))
  }

  @Test
  fun `traverse on map empty list`() {
    expectThat(Option.traverse(List.empty<Int>()) { a -> if (a % 2 == 0) some(a) else none() })
      .isEqualTo(some(List.empty()))
  }

  @Test
  fun `traverse on map function producing none`() {
    expectThat(Option.traverse(List.of(1, 2, 3, 4, 5)) { a -> if (a % 2 == 0) some(a) else none() })
      .isEqualTo(none())
  }

  @Test
  fun `traverse on map function producing only some`() {
    expectThat(Option.traverse(List.of(1, 2, 3, 4, 5)) { a -> some(a.toString()) })
      .isEqualTo(some(List.of("1", "2", "3", "4", "5")))
  }
}