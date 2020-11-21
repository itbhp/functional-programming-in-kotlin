package it.twinsbrains.chapter3

import assertk.assertThat
import assertk.assertions.isEqualTo
import org.junit.Test

class ListTest {

  @Test
  fun tailOnEmptyList() {
    val l = Nil

    assertThat(List.tail(l)).isEqualTo(Nil)
  }

  @Test
  fun tailOnNonEmptyList() {
    val l = List.of(1, 2, 3)

    assertThat(List.tail(l)).isEqualTo(List.of(2, 3))
  }

  @Test
  fun sum() {
    assertThat(List.sum(List.of(1, 2, 3, 4))).isEqualTo(10)
  }

  @Test
  fun product() {
    assertThat(List.product(List.of(1.0, 2.0, 3.0, 4.0))).isEqualTo(24.0)
  }

  @Test
  fun dropOnEmptyList() {
    assertThat(List.drop(Nil, 1)).isEqualTo(Nil)
  }

  @Test
  fun dropRemainingNonEmpty() {
    assertThat(List.drop(List.of(1, 2), 1)).isEqualTo(List.of(2))
  }

  @Test
  fun dropRemainingEmpty() {
    assertThat(List.drop(List.of(1, 2), 2)).isEqualTo(Nil)
  }

  @Test
  fun dropWhile() {
    assertThat(List.dropWhile(List.of(2, 4, 2, 3, 4, 5)) { n -> n % 2 == 0 })
      .isEqualTo(List.of(3, 4, 5))
  }

  @Test
  fun append() {
    assertThat(List.append(List.of(1, 2), List.of(3, 4))).isEqualTo(List.of(1, 2, 3, 4))
  }

  @Test
  fun initOnEmpty() {
    assertThat(List.init(Nil)).isEqualTo(Nil)
  }

  @Test
  fun initResultingEmpty() {
    assertThat(List.init(List.of(1))).isEqualTo(Nil)
  }

  @Test
  fun initResultingNonEmpty() {
    assertThat(List.init(List.of(1, 2, 3))).isEqualTo(List.of(1, 2))
  }
}