package it.twinsbrains.chapter3

import assertk.assertThat
import assertk.assertions.isEqualTo
import it.twinsbrains.chapter3.Tree.Companion.depth
import it.twinsbrains.chapter3.Tree.Companion.map
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

  @Test
  fun depthTest() {
    val tree = Branch(Leaf(1), Branch(Leaf(2), Leaf(3)))
    assertThat(depth(tree)).isEqualTo(3)
  }

  @Test
  fun mapTest() {
    val tree = Branch(Leaf(1), Branch(Leaf(2), Leaf(3)))
    val expected = Branch(Leaf(2), Branch(Leaf(4), Leaf(6)))
    assertThat(map(tree) { x -> x * 2 }).isEqualTo(expected)
  }
}