package it.twinsbrains.fpik.chapter3

import kotlin.math.max

sealed class Tree<out A> {
  companion object {
    fun <A> size(tree: Tree<A>): Int =
      fold(tree, { 1 }, { sizeLeft, sizeRight -> 1 + sizeLeft + sizeRight })

    fun <A> depth(tree: Tree<A>): Int =
      fold(tree, { 1 }, { depthLeft, depthRight -> 1 + max(depthLeft, depthRight) })

    fun maximum(tree: Tree<Int>): Int =
      fold(tree, { it }, { maximumLeft, maximumRight -> max(maximumLeft, maximumRight) })

    fun <A, B> map(t: Tree<A>, f: (A) -> B): Tree<B> =
      fold(t, { a -> leaf(f(a)) }, { tl, tr -> branch(tl, tr) })

    private fun <A, B> fold(ta: Tree<A>, l: (A) -> B, b: (B, B) -> B): B =
      when (ta) {
        is Leaf -> l(ta.value)
        is Branch -> b(fold(ta.left, l, b), fold(ta.right, l, b))
      }

    private fun <A> branch(l: Tree<A>, r: Tree<A>): Tree<A> = Branch(l, r)
    private fun <A> leaf(v: A): Tree<A> = Leaf(v)
  }
}

data class Leaf<A>(val value: A) : Tree<A>()
data class Branch<A>(
  val left: Tree<A>,
  val right: Tree<A>
) : Tree<A>()