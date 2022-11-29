package it.twinsbrains.fpik.chapter12

import arrow.Kind
import arrow.core.ForId
import arrow.core.ForOption
import arrow.core.Id
import arrow.core.IdOf
import arrow.core.fix
import arrow.core.none
import arrow.core.some
import arrow.core.value
import it.twinsbrains.fpik.chapter11.Functor
import it.twinsbrains.fpik.chapter3.ForList
import it.twinsbrains.fpik.chapter3.List
import it.twinsbrains.fpik.chapter3.fix


val idApplicative: Applicative<ForId> = object : Applicative<ForId> {
  override fun <A> unit(a: A): IdOf<A> = Id(a)

  override fun <A, B> apply(fab: Kind<ForId, (A) -> B>, fa: Kind<ForId, A>): Kind<ForId, B> {
    val f = fab.value()
    val a = fa.value()
    return Id(f(a))
  }
}

@Suppress("FunctionParameterNaming")
interface Traversable<F> : Functor<F> {

  override fun <A, B> map(
    fa: Kind<F, A>,
    f: (A) -> B
  ): Kind<F, B> = traverse(fa, idApplicative) { a: A -> Id(f(a)) }.value()

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

class ForTree
typealias TreeOf<A> = Kind<ForTree, A>

object TraversableExamples {
  data class Tree<out A>(val head: A, val tail: List<Tree<A>>) : TreeOf<A> {
    companion object
  }

  fun <A> TreeOf<A>.fix(): Tree<A> = this as Tree<A>
  fun <A> optionTraversable(): Traversable<ForOption> = object : Traversable<ForOption> {
    override fun <A, B> map(fa: Kind<ForOption, A>, f: (A) -> B): Kind<ForOption, B> = fa.fix().map(f)

    override fun <G, A> sequence(fga: Kind<ForOption, Kind<G, A>>, AG: Applicative<G>): Kind<G, Kind<ForOption, A>> =
      fga.fix().fold(
        { AG.unit(none()) },
        { ga: Kind<G, A> ->
          AG.map2(ga, AG.unit(Unit)) { a: A, _ -> a.some() }
        }
      )
  }

  fun <A> listTraversable(): Traversable<ForList> = object : Traversable<ForList> {
    override fun <A, B> map(fa: Kind<ForList, A>, f: (A) -> B): Kind<ForList, B> = List.map(fa.fix(), f)

    override fun <G, A> sequence(fga: Kind<ForList, Kind<G, A>>, AG: Applicative<G>): Kind<G, Kind<ForList, A>> =
      List.foldRight(fga.fix(), AG.unit(List.empty())) { ge: Kind<G, A>, acc: Kind<G, List<A>> ->
        AG.map2(ge, acc) { e: A, list: List<A> -> List.cons(e, list) }
      }
  }

  fun <A> treeTraversable(): Traversable<ForTree> = TODO()
}