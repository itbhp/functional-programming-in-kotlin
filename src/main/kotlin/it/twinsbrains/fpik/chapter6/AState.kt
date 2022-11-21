package it.twinsbrains.fpik.chapter6

import arrow.Kind
import it.twinsbrains.fpik.chapter11.Monad

class ForAState
typealias AStatePartialOf<S> = Kind<ForAState, S>
typealias AStateOf<S, A> = Kind<AStatePartialOf<S>, A>

fun <S, A> AStateOf<S, A>.fix(): AState<S, A> = this as AState<S, A>

interface StateMonad<S> : Monad<AStatePartialOf<S>> {
  override fun <A> unit(a: A): AStateOf<S, A>
}

data class AState<S, out A>(val myRun: (S) -> Pair<A, S>) : AStateOf<S, A> {

  companion object {

    fun <S> monad(): StateMonad<S> = object : StateMonad<S> {
      override fun <A, B> flatMap(
        fa: AStateOf<S, A>,
        f: (A) -> AStateOf<S, B>
      ): AStateOf<S, B> = Companion.flatMap(fa, f)

      override fun <A> unit(a: A): AStateOf<S, A> = Companion.unit(a)
    }

    fun <S, A> unit(a: A): AState<S, A> = AState { s -> a to s }

    fun <S, A, B> flatMap(fa: AStateOf<S, A>, f: (A) -> AStateOf<S, B>): AState<S, B> =
      AState { s ->
        val (a, s2) = fa.fix().myRun(s)
        f(a).fix().myRun(s2)
      }

    fun <S, A, B> map(s: AState<S, A>, f: (A) -> B): AState<S, B> =
      flatMap(s) { a -> AState { s -> f(a) to s } }

    fun <A, B, C> map2(
      ra: Rand<A>,
      rb: Rand<B>,
      f: (A, B) -> C
    ): Rand<C> = flatMap(ra) { a -> map(rb) { b -> f(a, b) } }

    fun <A> sequence(fs: List<Rand<A>>): Rand<List<A>> =
      fs.fold(unit(listOf())) { acc, r -> map2(acc, r) { l, a -> l + a } }
  }
}