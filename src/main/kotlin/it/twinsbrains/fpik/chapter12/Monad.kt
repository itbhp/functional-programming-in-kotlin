package it.twinsbrains.fpik.chapter12

import arrow.Kind

interface Monad<F> : Applicative<F> {
  fun <A, B> flatMap(fa: Kind<F, A>, f: (A) -> Kind<F, B>): Kind<F, B> =
    join(map(fa, f))

  fun <A> join(ffa: Kind<F, Kind<F, A>>): Kind<F, A> // = flatMap(ffa) { fa -> fa }

  fun <A, B, C> compose(
    f: (A) -> Kind<F, B>,
    g: (B) -> Kind<F, C>
  ): (A) -> Kind<F, C> =
    { a -> flatMap(f(a), g) }

  override fun <A, B> map(
    fa: Kind<F, A>,
    f: (A) -> B
  ): Kind<F, B> =
    flatMap(fa) { a -> unit(f(a)) }

  override fun <A, B, C> map2(
    fa: Kind<F, A>,
    fb: Kind<F, B>,
    f: (A, B) -> C
  ): Kind<F, C> =
    flatMap(fa) { a -> map(fb) { b -> f(a, b) } }
}