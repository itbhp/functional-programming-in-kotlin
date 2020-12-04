package it.twinsbrains.fpik.chapter6

interface RNG {
    fun nextInt(): Pair<Int, RNG>
}

typealias Rand<A> = (RNG) -> Pair<A, RNG>

fun <A> unit(a: A): Rand<A> = { rng -> Pair(a, rng) }

fun <A, B> map(s: Rand<A>, f: (A) -> B): Rand<B> = { rng ->
    val (a, rng2) = s(rng)
    Pair(f(a), rng2)
}

object RandExamples {
    val intR: Rand<Int> = { rng -> rng.nextInt() }
    val nonNegativeIntR: Rand<Int> = map(intR) { if (it < 0) -(it + 1) else it }
    val doubleR: Rand<Double> = map(nonNegativeIntR) { it.toDouble() / Int.MAX_VALUE }
}