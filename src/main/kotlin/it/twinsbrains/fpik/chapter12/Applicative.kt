package it.twinsbrains.fpik.chapter12

import arrow.Kind
import it.twinsbrains.fpik.chapter11.Functor
import it.twinsbrains.fpik.chapter2.Currying.curry

interface Applicative<F> : Functor<F> {
  fun <A> unit(a: A): Kind<F, A>

  fun <A, B> apply(
    fab: Kind<F, (A) -> B>,
    fa: Kind<F, A>
  ): Kind<F, B> // = map2(fa, fab) { a: A, f: (A) -> B -> f(a) }

  override fun <A, B> map(fa: Kind<F, A>, f: (A) -> B): Kind<F, B> =
    apply(unit(f), fa)

  fun <A, B, C> map2(
    fa: Kind<F, A>,
    fb: Kind<F, B>,
    f: (A, B) -> C
  ): Kind<F, C> {
    val fCurried: (A) -> (B) -> C = curry(f)
    val fbc: Kind<F, (B) -> C> = apply(unit(fCurried), fa)
    return apply(fbc, fb)
  }

  fun <A, B, C, D> map3(
    fa: Kind<F, A>,
    fb: Kind<F, B>,
    fc: Kind<F, C>,
    f: (A, B, C) -> D
  ): Kind<F, D> {
    val fCurried: (A) -> (B) -> (C) -> D = curry(f)
    val fbcd: Kind<F, (B) -> (C) -> D> = apply(unit(fCurried), fa)
    val fcd: Kind<F, (C) -> D> = apply(fbcd, fb)
    return apply(fcd, fc)
  }

  fun <A, B> traverse(
    la: List<A>,
    f: (A) -> Kind<F, B>
  ): Kind<F, List<B>> =
    la.foldRight(
      unit(listOf())
    ) { a: A, acc: Kind<F, List<B>> ->
      map2(f(a), acc) { b: B, lb: List<B> -> listOf(b) + lb }
    }

  fun <A> sequence(lfa: List<Kind<F, A>>): Kind<F, List<A>> = traverse(lfa) { it }

  fun <A> replicateM(n: Int, ma: Kind<F, A>): Kind<F, List<A>> =
    sequence(List(n) { ma })

  fun <A, B> product(
    ma: Kind<F, A>,
    mb: Kind<F, B>
  ): Kind<F, Pair<A, B>> = map2(ma, mb) { a, b -> a to b }
}


class ForProduct
typealias ProductPartialOf<F, G> = Kind<Kind<ForProduct, F>, G>
typealias ProductOf<F, G, A> = Kind<ProductPartialOf<F, G>, A>

data class Product<F, G, A>(val value: Pair<Kind<F, A>, Kind<G, A>>) : ProductOf<F, G, A>

fun <F, G, A> ProductOf<F, G, A>.fix(): Product<F, G, A> = this as Product<F, G, A>

fun <F, G> product(
  AF: Applicative<F>,
  AG: Applicative<G>
): Applicative<ProductPartialOf<F, G>> =
  object : Applicative<ProductPartialOf<F, G>> {

    override fun <A, B> apply(
      fgab: ProductOf<F, G, (A) -> B>,
      fga: ProductOf<F, G, A>
    ): ProductOf<F, G, B> {
      val (fab, gab) = fgab.fix().value
      val (fa, ga) = fga.fix().value
      return Product(Pair(AF.apply(fab, fa), AG.apply(gab, ga)))
    }

    override fun <A> unit(a: A): ProductOf<F, G, A> =
      Product(Pair(AF.unit(a), AG.unit(a)))
  }