package it.twinsbrains.fpik.chapter8

import arrow.core.Tuple2
import arrow.core.getOrElse
import arrow.core.toOption
import arrow.mtl.*
import it.twinsbrains.fpik.chapter6.RNG
import it.twinsbrains.fpik.chapter6.Randoms.double
import it.twinsbrains.fpik.chapter6.Randoms.nonNegativeInt
import it.twinsbrains.fpik.chapter7.Par
import it.twinsbrains.fpik.chapter8.Gen.Companion.combine
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import kotlin.math.absoluteValue
import kotlin.math.max
import kotlin.math.min
import it.twinsbrains.fpik.chapter6.LinearCongruentialGenerator as SimpleRNG

data class SGen<A>(val forSize: (Int) -> Gen<A>) {
  operator fun invoke(i: Int): Gen<A> = forSize(i)
  fun <B> map(f: (A) -> B): SGen<B> = SGen { n: Int ->
    forSize(n).map(f)
  }

  fun <B> flatMap(f: (A) -> Gen<B>): SGen<B> = SGen { n: Int ->
    forSize(n).flatMap(f)
  }

  companion object {
    fun <A> nonEmptyListOf(ga: Gen<A>): SGen<List<A>> = SGen { n: Int ->
      Gen.listOfN(max(1, n), ga)
    }
  }
}

data class Gen<A>(val sample: State<RNG, A>) {

  fun listOf(): SGen<List<A>> = SGen { n ->
    listOfN(n, this)
  }

  fun unsized(): SGen<A> = SGen { this }

  fun <B> map(f: (A) -> B): Gen<B> = Gen(sample.map(f))

  fun <B> flatMap(f: (A) -> Gen<B>): Gen<B> = Gen(sample.flatMap { valA -> f(valA).sample })

  companion object {

    private fun <A, B, C> map2(a: Gen<A>, b: Gen<B>, f: (A, B) -> C): Gen<C> =
      a.flatMap { vA -> b.map { vB -> f(vA, vB) } }

    infix fun <A, B> Gen<A>.combine(gb: Gen<B>): Gen<Pair<A, B>> =
      map2(this, gb) { s, a -> s to a }

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

object Proved : Result() {
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

typealias MaxSize = Int
typealias TestCases = Int

data class Prop(val check: (MaxSize, TestCases, RNG) -> Result) {

  companion object {

    fun check(p: () -> Boolean): Prop =
      Prop { _, _, _ ->
        if (p()) Proved
        else Falsified("()", 0)
      }

    fun run(
      p: Prop,
      maxSize: Int = 100,
      testCases: Int = 100,
      rng: RNG = SimpleRNG(System.currentTimeMillis())
    ): Result =
      when (val result = p.check(maxSize, testCases, rng)) {
        is Falsified -> result.also {
          println(
            "Falsified after ${it.successes}" +
              "passed tests: ${it.failure}"
          )
        }
        is Passed -> result.also {
          println("OK, passed $testCases tests.")
        }
        is Proved -> result.also { println("OK, proved property.") }
      }
  }

  fun and(p: Prop): Prop = Prop { m: MaxSize, n: TestCases, rng: RNG ->

    when (val check1 = this.check(m, n, rng)) {
      is Falsified -> check1
      else -> p.check(m, n, rng)
    }
  }

  fun or(p: Prop): Prop = Prop { m: MaxSize, n: TestCases, rng: RNG ->
    when (val check1 = this.check(m, n, rng)) {
      is Passed, is Proved -> check1
      is Falsified -> p.check(m, n, rng)
    }
  }
}

object Checkers {

  fun checkPar(p: Par<Boolean>): Prop =
    forAllPar(Gen.unit(Unit)) { p }

  private val ges: Gen<ExecutorService> = Gen.weighted(
    Gen.choose(1, 4).map {
      Executors.newFixedThreadPool(it)
    } to .75,
    Gen.unit(
      Executors.newCachedThreadPool()
    ) to .25)

  fun <A> forAllPar(ga: Gen<A>, f: (A) -> Par<Boolean>): Prop =
    forAll(ges combine ga) { (es, a) ->
      f(a)(es).get()
    }

  fun <A> forAll(g: SGen<A>, f: (A) -> Boolean): Prop =
    forAll({ i -> g(i) }, f)

  fun <A> forAll(g: (Int) -> Gen<A>, f: (A) -> Boolean): Prop =
    Prop { max, n, rng ->
      val casePerSize: Int = (n + (max - 1)) / max
      val props: Sequence<Prop> =
        generateSequence(0) { it + 1 }
          .take(min(n, max) + 1)
          .map { i -> forAll(g(i), f) }
      val prop: Prop = props.map { p ->
        Prop { max, _, rng ->
          p.check(max, casePerSize, rng)
        }
      }.reduce { p1, p2 -> p1.and(p2) }
      prop.check(max, n, rng)
    }

  fun <A> forAll(ga: Gen<A>, f: (A) -> Boolean): Prop =
    Prop { _: MaxSize, n: TestCases, rng: RNG ->
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