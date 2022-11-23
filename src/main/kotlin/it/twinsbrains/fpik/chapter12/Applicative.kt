package it.twinsbrains.fpik.chapter12

import arrow.Kind
import it.twinsbrains.fpik.chapter11.Functor

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
    val fbc = map(fa) { a ->
      { b: B -> f(a, b) }
    }
    return apply(fbc, fb)
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
}