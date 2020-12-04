package it.twinsbrains.fpik.chapter3

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import strikt.api.expectThat
import strikt.assertions.isEqualTo

class ListTest {

    @Test
    fun tailOnEmptyList() {
      expectThat(List.tail(Nil)).isEqualTo(Nil)
    }

    @Test
    fun tailOnNonEmptyList() {
      expectThat(List.tail(List.of(1, 2, 3))).isEqualTo(List.of(2, 3))
    }

    @Test
    fun sum() {
      expectThat(List.sum(List.of(1, 2, 3, 4))).isEqualTo(10)
    }

    @Test
    fun product() {
      expectThat(List.product(List.of(1.0, 2.0, 3.0, 4.0))).isEqualTo(24.0)
    }

    @Test
    fun dropOnEmptyList() {
      expectThat(List.drop(Nil, 1)).isEqualTo(Nil)
    }

    @Test
    fun dropRemainingNonEmpty() {
      expectThat(List.drop(List.of(1, 2), 1)).isEqualTo(List.of(2))
    }

    @Test
    fun dropRemainingEmpty() {
      expectThat(List.drop(List.of(1, 2), 2)).isEqualTo(Nil)
    }

    @Test
    fun dropWhile() {
      expectThat(List.dropWhile(List.of(2, 4, 2, 3, 4, 5)) { n -> n % 2 == 0 })
            .isEqualTo(List.of(3, 4, 5))
    }

    @Test
    fun append() {
      expectThat(List.append(List.of(1, 2), List.of(3, 4))).isEqualTo(List.of(1, 2, 3, 4))
    }

    @Test
    fun initOnEmpty() {
        assertThrows<IllegalArgumentException> { List.init(Nil) }
    }

    @Test
    fun initResultingEmpty() {
      expectThat(List.init(List.of(1))).isEqualTo(Nil)
    }

    @Test
    fun initResultingNonEmpty() {
      expectThat(List.init(List.of(1, 2, 3))).isEqualTo(List.of(1, 2))
    }

    @Test
    fun lengthOnEmpty() {
      expectThat(List.length(List.empty<Int>())).isEqualTo(0)
    }

    @Test
    fun lengthOnNonEmpty() {
      expectThat(List.length(List.of(1, 2, 3, 4))).isEqualTo(4)
    }

    @Test
    fun reverse() {
      expectThat(
        List.reverse(
          List.of(1, 2, 3)
        )
      ).isEqualTo(List.of(3, 2, 1))
    }

    @Test
    fun `foldRight using foldLeft`() {
      expectThat(
        List.foldRight(
          List.of(1, 2, 3),
          List.empty(),
          { e: Int, acc: List<String> -> Cons(e.toString(), acc) })
      ).isEqualTo(List.of("1", "2", "3"))
    }

    @Test
    fun concatenate() {
      expectThat(
        List.concatenate(
          List.of(
            List.of(1, 2, 3),
            List.of(4, 5, 6)
          )
        )
      ).isEqualTo(List.of(1, 2, 3, 4, 5, 6))
    }

    @Test
    fun `add one to each element of a list`() {
      expectThat(
        List.addOne(List.of(1, 2, 3, 4))
      ).isEqualTo(List.of(2, 3, 4, 5))
    }

    @Test
    fun filter() {
      expectThat(
        List.filter(List.of(1, 2, 3)) { x -> x % 2 != 0 }
      ).isEqualTo(List.of(1, 3))
    }

    @Test
    fun flatMap() {
      expectThat(
        List.flatMap(
          List.of(1, 2, 3, 4)
        ) { x -> Cons(x, Nil) }
      ).isEqualTo(List.of(1, 2, 3, 4))
    }

    @Test
    fun zipWith() {
      expectThat(
        List.zipWith(List.of(1, 2, 3), List.of(2.0, 3.0, 4.0)) { i, d -> i * d }
      ).isEqualTo(List.of(2.0, 6.0, 12.0))
    }

    @Test
    fun hasSubsequenceOnEmpty() {
      expectThat(
        List.hasSubsequence(
          List.of(1, 2, 3, 4, 5, 6, 7, 8, 9),
          List.empty()
        )
      ).isEqualTo(true)
    }

    @Test
    fun hasSubsequenceOnBothEmpty() {
      expectThat(
        List.hasSubsequence(
          List.empty<String>(),
          List.empty()
        )
      ).isEqualTo(true)
    }

    @Test
    fun hasSubsequenceFromEmpty() {
      expectThat(
        List.hasSubsequence(
          List.empty(),
          List.of(1, 2, 3, 4, 5, 6, 7, 8, 9)
        )
      ).isEqualTo(false)
    }

    @Test
    fun hasSubsequence() {
      expectThat(
        List.hasSubsequence(
          List.of(1, 2, 3, 4, 5, 6, 7, 8, 9),
          List.of(4, 5, 6, 7)
        )
      ).isEqualTo(true)
    }
}