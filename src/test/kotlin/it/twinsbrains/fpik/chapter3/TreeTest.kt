package it.twinsbrains.fpik.chapter3

import it.twinsbrains.fpik.chapter3.Tree.Companion.depth
import it.twinsbrains.fpik.chapter3.Tree.Companion.map
import it.twinsbrains.fpik.chapter3.Tree.Companion.maximum
import it.twinsbrains.fpik.chapter3.Tree.Companion.size
import org.junit.jupiter.api.Test
import strikt.api.expectThat
import strikt.assertions.isEqualTo

class TreeTest {
  @Test
  fun sizeTest() {
    val tree = Branch(Leaf(1), Branch(Leaf(2), Leaf(3)))
    expectThat(size(tree)).isEqualTo(5)
  }

  @Test
  fun maximumTest() {
    val tree = Branch(Leaf(1), Branch(Leaf(2), Leaf(3)))
    expectThat(maximum(tree)).isEqualTo(3)
  }

  @Test
  fun depthTest() {
    val tree = Branch(Leaf(1), Branch(Leaf(2), Leaf(3)))
    expectThat(depth(tree)).isEqualTo(3)
  }

  @Test
  fun mapTest() {
    val tree = Branch(Leaf(1), Branch(Leaf(2), Leaf(3)))
    val expected = Branch(Leaf(2), Branch(Leaf(4), Leaf(6)))
    expectThat(map(tree) { x -> x * 2 }).isEqualTo(expected)
  }
}