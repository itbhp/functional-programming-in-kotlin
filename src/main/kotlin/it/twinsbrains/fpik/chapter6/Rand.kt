package it.twinsbrains.fpik.chapter6

import it.twinsbrains.fpik.chapter6.RandCompanion.flatMap
import it.twinsbrains.fpik.chapter6.RandCompanion.map
import it.twinsbrains.fpik.chapter6.RandCompanion.map2
import it.twinsbrains.fpik.chapter6.RandCompanion.sequence
import it.twinsbrains.fpik.chapter6.RandCompanion.unit

typealias Rand<A> = (RNG) -> Pair<A, RNG>

interface RNG {
    fun nextInt(): Pair<Int, RNG>
}

object RandCompanion {
    fun <A> unit(a: A): Rand<A> = { rng -> Pair(a, rng) }

    fun <A, B> map(s: Rand<A>, f: (A) -> B): Rand<B> = flatMap(s) { a -> { rng -> f(a) to rng } }

    fun <A, B, C> map2(
        ra: Rand<A>,
        rb: Rand<B>,
        f: (A, B) -> C
    ): Rand<C> = flatMap(ra) { a -> flatMap(rb) { b -> { rng -> f(a, b) to rng } } }

    fun <A> sequence(fs: List<Rand<A>>): Rand<List<A>> = { rng ->
        fs.fold(listOf<A>() to rng) { acc, randA ->
            val (l, r) = acc
            val (a, rng1) = randA(r)
            (l + a) to rng1
        }
    }

    fun <A, B> flatMap(f: Rand<A>, g: (A) -> Rand<B>): Rand<B> = { rng ->
        val (a, rng1) = f(rng)
        g(a)(rng1)
    }
}

object RandExamples {
    private val intR: Rand<Int> = unit(3)
    private val nonNegativeIntR: Rand<Int> = map(intR) { if (it < 0) -(it + 1) else it }
    val doubleR: Rand<Double> = map(nonNegativeIntR) { it.toDouble() / Int.MAX_VALUE }
    val intDoubleR: Rand<Pair<Int, Double>> = map2(nonNegativeIntR, doubleR) { a, b -> a to b }
    fun intsR(count: Int): Rand<List<Int>> = sequence((1..count).map { nonNegativeIntR })
    fun nonNegativeIntLessThan(n: Int): Rand<Int> = flatMap(nonNegativeIntR) { i ->
        val mod = i % n
        if (i + (n - 1) - mod >= 0) {
            unit(mod)
        } else { rng ->
            nonNegativeIntLessThan(n)(rng)
        }
    }
}