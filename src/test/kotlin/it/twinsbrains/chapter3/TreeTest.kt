package it.twinsbrains.chapter3

import assertk.assertThat
import assertk.assertions.isEqualTo
import it.twinsbrains.chapter3.Tree.Companion.maximum
import it.twinsbrains.chapter3.Tree.Companion.size
import org.junit.Test

class TreeTest {
  @Test
  fun sizeTest() {
    val tree = Branch(Leaf(1), Branch(Leaf(2), Leaf(3)))
    assertThat(size(tree)).isEqualTo(5)
  }

  @Test
  fun maximumTest() {
    val tree = Branch(Leaf(1), Branch(Leaf(2), Leaf(3)))
    assertThat(maximum(tree)).isEqualTo(3)
  }
}