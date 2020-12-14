package it.twinsbrains.fpik.chapter8

import arrow.core.Tuple2
import arrow.core.getOrElse
import arrow.core.toOption
import arrow.mtl.*
import it.twinsbrains.fpik.chapter6.RNG
import it.twinsbrains.fpik.chapter6.Randoms.double
import it.twinsbrains.fpik.chapter6.Randoms.nonNegativeInt
import kotlin.math.absoluteValue

data class Gen<A>(val sample: State<RNG, A>) {

  fun <B> flatMap(f: (A) -> Gen<B>): Gen<B> = Gen(sample.flatMap { valA -> f(valA).sample })

  companion object {

    fun <A> weighted(
      pga: Pair<Gen<A>, Double>,
      pgb: Pair<Gen<A>, Double>
    ): Gen<A> {
      val (ga, p1) = pga
      val (gb, p2) = pgb
      val prob =
        p1.absoluteValue /
          (p1.absoluteValue + p2.absoluteValue)
      return Gen(State { rng: RNG -> double(rng).flip() })
        .flatMap { d ->
          if (d < prob) ga else gb
        }
    }

    fun <A> union(ga: Gen<A>, gb: Gen<A>): Gen<A> = boolean().flatMap { if (it) ga else gb }

    fun <A> listOfN(gn: Gen<Int>, ga: Gen<A>): Gen<List<A>> =
      gn.flatMap { n -> listOfN(n, ga) }

    fun choose(start: Int, stopExclusive: Int): Gen<Int> {
      val s = State { rng: RNG -> nonNegativeInt(rng).flip() }
        .map { i ->
          val r = stopExclusive - start
          (i % r) + start
        }
      return Gen(s)
    }

    fun <A> unit(a: A): Gen<A> = Gen(StateApi.just(a))

    fun boolean(): Gen<Boolean> = Gen(State { rng ->
      val (i, nRng) = rng.nextInt()
      Tuple2(nRng, i < 0)
    })

    fun <A> listOfN(n: Int, ga: Gen<A>): Gen<List<A>> =
//      Gen(
//        State { rng ->
//          val (nRng, l) = (1..n)
//            .fold(Pair(rng, listOf<A>())) { acc, _ ->
//              val (iRng, l) = acc
//              val tuple2 = ga.sample.run(iRng)
//              tuple2.a to l + tuple2.b
//            }
//          Tuple2(nRng, l)
//        }
//      )
      Gen((1..n).map { ga.sample }.stateSequential())
  }
}

private fun <A, B> Pair<A, B>.flip(): Tuple2<B, A> = Tuple2(this.second, this.first)


sealed class Result {
  abstract fun isFalsified(): Boolean
}

object Passed : Result() {
  override fun isFalsified(): Boolean = false
}

data class Falsified(
  val failure: FailedCase,
  val successes: SuccessCount
) : Result() {
  override fun isFalsified(): Boolean = true
}

typealias SuccessCount = Int
typealias FailedCase = String

typealias TestCases = Int

data class Prop(val check: (TestCases, RNG) -> Result) {
  fun and(p: Prop): Prop = Prop { n: TestCases, rng: RNG ->
    val check1 = this.check(n, rng)
    if (Passed != check1) {
      check1
    } else {
      p.check(n, rng)
    }
  }

  fun or(p: Prop): Prop = Prop { n: TestCases, rng: RNG ->
    val check1 = this.check(n, rng)
    if (Passed == check1) {
      check1
    } else {
      p.check(n, rng)
    }
  }
}

object Checkers {
  fun <A> forAll(ga: Gen<A>, f: (A) -> Boolean): Prop =
    Prop { n: TestCases, rng: RNG ->
      randomSequence(ga, rng).mapIndexed { i, a ->
        try {
          if (f(a)) Passed
          else Falsified(a.toString(), i)
        } catch (e: Exception) {
          Falsified(buildMessage(a, e), i)
        }
      }.take(n)
        .find { it.isFalsified() }
        .toOption()
        .getOrElse { Passed }
    }

  private fun <A> randomSequence(
    ga: Gen<A>,
    rng: RNG
  ): Sequence<A> =
    sequence {
      val (rng2: RNG, valA: A) = ga.sample.run(rng)
      yield(valA)
      yieldAll(randomSequence(ga, rng2))
    }

  private fun <A> buildMessage(a: A, e: Exception) =
    """
    |test case: $a
    |generated and exception: ${e.message}
    |stacktrace:
    |${e.stackTrace.joinToString("\n")}
""".trimMargin()
}