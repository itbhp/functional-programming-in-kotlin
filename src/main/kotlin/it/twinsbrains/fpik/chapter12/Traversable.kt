package it.twinsbrains.fpik.chapter12

import arrow.Kind
import it.twinsbrains.fpik.chapter11.Functor

@Suppress("FunctionParameterNaming")
interface Traversable<F> : Functor<F> {
  fun <G, A, B> traverse(
    fa: Kind<F, A>,
    AG: Applicative<G>,
    f: (A) -> Kind<G, B>
  ): Kind<G, Kind<F, B>> =
    sequence(map(fa, f), AG)

  fun <G, A> sequence(
    fga: Kind<F, Kind<G, A>>,
    AG: Applicative<G>
  ): Kind<G, Kind<F, A>> // = traverse(fga, AG) { it }
}

// class ForTree
// typealias TreeOf<A> = Kind<ForTree, A>
//
// object TraversableExamples {
//  data class Tree<out A>(val head: A, val tail: List<Tree<A>>) : TreeOf<A> {
//    companion object
//  }
//
//  fun <A> TreeOf<A>.fix(): Tree<A> = this as Tree<A>
//  fun <A> optionTraversable(): Traversable<ForOption> = object : Traversable<ForOption>{
//
//  }
//  fun <A> listTraversable(): Traversable<ForList> = TODO()
//  fun <A> treeTraversable(): Traversable<ForTree> = TODO()
// }