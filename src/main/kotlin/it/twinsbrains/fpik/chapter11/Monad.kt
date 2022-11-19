package it.twinsbrains.fpik.chapter11

import arrow.Kind

interface Monad<F> : Functor<F> {

  fun <A> unit(a: A): Kind<F, A>

  fun <A, B> flatMap(fa: Kind<F, A>, f: (A) -> Kind<F, B>): Kind<F, B> =
    compose<Unit, A, B>({ _ -> fa }, f)(Unit)

  override fun <A, B> map(fa: Kind<F, A>, f: (A) -> B): Kind<F, B> =
    flatMap(fa) { a -> unit(f(a)) }

  fun <A, B, C> map2(fa: Kind<F, A>, fb: Kind<F, B>, f: (A, B) -> C): Kind<F, C> =
    flatMap(fa) { a -> map(fb) { b -> f(a, b) } }


  // compose kleisli arrows
  fun <A, B, C> compose(
    f: (A) -> Kind<F, B>,
    g: (B) -> Kind<F, C>
  ): (A) -> Kind<F, C>
//    = { a: A ->
//    flatMap(f(a)) { b -> g(b) }
//  }

  fun <A> replicateM(n: Int, fa: Kind<F, A>): List<Kind<F, A>>

  fun <A> filterM(ls: List<A>, f: (A) -> Kind<F, Boolean>): Kind<F, List<A>> =
    when {
      ls.isEmpty() -> unit(listOf<A>())
      else -> flatMap(f(ls[0])) { toKeep ->
        if (toKeep) {
          map(filterM(ls.subList(1, ls.size), f)) { t -> listOf(ls[0]) + t }
        } else {
          filterM(ls.subList(1, ls.size), f)
        }
      }
    }
}