package it.twinsbrains.fpik.chapter8

import arrow.core.Either
import arrow.core.Tuple2
import arrow.mtl.State
import it.twinsbrains.fpik.chapter6.RNG

data class Gen<A>(val sample: State<RNG, A>) {
  companion object {
    fun choose(start: Int, stopExclusive: Int): Gen<Int> {
      val s = State { rng: RNG ->
        val (i, nRng) = rng.nextInt()
        val r = stopExclusive - start
        Tuple2(nRng, (i % r) + start)
      }
      return Gen(s)
    }
  }
}

typealias SuccessCount = Int
typealias FailedCase = String

interface Prop {
  fun check(): Either<Pair<FailedCase, SuccessCount>, SuccessCount>
  fun and(p: Prop): Prop
}

object Generators {
  fun <A> listOf(a: Gen<A>): List<Gen<A>> = TODO()

  fun <A> listOfN(n: Int, a: Gen<A>): List<Gen<A>> = TODO()

  fun <A> forAll(a: Gen<A>, f: (A) -> Boolean): Prop = TODO()
}