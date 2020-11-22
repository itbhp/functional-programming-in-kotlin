package it.twinsbrains.chapter3

import kotlin.math.max

sealed class Tree<out A> {
  companion object {
    fun <A> size(tree: Tree<A>): Int =
//      when (tree) {
//        is Leaf -> 1
//        is Branch -> 1 + size(tree.left) + size(tree.right)
//      }
      fold(tree, { 1 }, { sizeLeft, sizeRight -> 1 + sizeLeft + sizeRight })

    fun <A> depth(tree: Tree<A>): Int =
//      when (tree) {
//        is Leaf -> 1
//        is Branch -> 1 + max(depth(tree.left), depth(tree.right))
//      }
      fold(tree, { 1 }, { depthLeft, depthRight -> 1 + max(depthLeft, depthRight) })

    fun maximum(tree: Tree<Int>): Int =
//      when (tree) {
//        is Leaf -> tree.value
//        is Branch -> max(maximum(tree.left), maximum(tree.right))
//      }
      fold(tree, { it }, { maximumLeft, maximumRight -> max(maximumLeft, maximumRight) })

    fun <A, B> map(t: Tree<A>, f: (A) -> B): Tree<B> =
//      when (t) {
//        is Leaf -> Leaf(f(t.value))
//        is Branch -> Branch(map(t.left, f), map(t.right, f))
//      }
      fold(t, { a -> Leaf(f(a)) as Tree<B> }, { tl, tr -> Branch(tl, tr) })

    fun <A, B> fold(ta: Tree<A>, l: (A) -> B, b: (B, B) -> B): B =
      when (ta) {
        is Leaf -> l(ta.value)
        is Branch -> b(fold(ta.left, l, b), fold(ta.right, l, b))
      }

  }
}

data class Leaf<A>(val value: A) : Tree<A>()
data class Branch<A>(
  val left: Tree<A>,
  val right: Tree<A>
) : Tree<A>()