package it.twinsbrains.fpik.chapter8

import arrow.core.Either
import arrow.core.Tuple2
import arrow.core.extensions.IdApplicative
import arrow.core.extensions.IdFunctor
import arrow.mtl.State
import it.twinsbrains.fpik.chapter6.RNG
import it.twinsbrains.fpik.chapter6.Randoms.nonNegativeInt

data class Gen<A>(val sample: State<RNG, A>) {
  companion object {
    fun choose(start: Int, stopExclusive: Int): Gen<Int> {
      val s = State { rng: RNG -> nonNegativeInt(rng).flip() }
        .map(object : IdFunctor {}) { i ->
          val r = stopExclusive - start
          (i % r) + start
        }
      return Gen(s)
    }

    fun <A> unit(a: A): Gen<A> = Gen(State.just(object : IdApplicative {}, a))

    fun boolean(): Gen<Boolean> = TODO()
    fun <A> listOfN(n: Int, ga: Gen<A>): Gen<List<A>> = TODO()
  }
}

private fun <A, B> Pair<A, B>.flip(): Tuple2<B, A> = Tuple2(this.second, this.first)

typealias SuccessCount = Int
typealias FailedCase = String

interface Prop {
  fun check(): Either<Pair<FailedCase, SuccessCount>, SuccessCount>
  fun and(p: Prop): Prop
}

object Checkers {
  fun <A> forAll(a: Gen<A>, f: (A) -> Boolean): Prop = TODO()
}