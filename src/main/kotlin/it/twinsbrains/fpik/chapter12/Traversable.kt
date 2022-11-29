package it.twinsbrains.fpik.chapter12

import arrow.Kind
import it.twinsbrains.fpik.chapter11.Functor

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