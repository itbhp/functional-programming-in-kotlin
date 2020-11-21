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
}