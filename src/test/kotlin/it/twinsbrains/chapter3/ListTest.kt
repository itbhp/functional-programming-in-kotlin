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
}