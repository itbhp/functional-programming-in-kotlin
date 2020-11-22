package it.twinsbrains.chapter3

sealed class Tree<out A> {
  companion object {
    fun <A> size(tree: Tree<A>): Int =
      when (tree) {
        is Leaf -> 1
        is Branch -> 1 + size(tree.left) + size(tree.right)
      }
  }
}

data class Leaf<A>(val value: A) : Tree<A>()
data class Branch<A>(
  val left: Tree<A>,
  val right: Tree<A>
) : Tree<A>()