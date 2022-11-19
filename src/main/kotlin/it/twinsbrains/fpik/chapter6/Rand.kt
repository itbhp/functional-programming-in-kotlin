package it.twinsbrains.fpik.chapter6

import it.twinsbrains.fpik.chapter6.AState.Companion.flatMap
import it.twinsbrains.fpik.chapter6.AState.Companion.map
import it.twinsbrains.fpik.chapter6.AState.Companion.map2
import it.twinsbrains.fpik.chapter6.AState.Companion.sequence
import it.twinsbrains.fpik.chapter6.AState.Companion.unit

typealias Rand<A> = AState<RNG, A>

interface RNG {
    fun nextInt(): Pair<Int, RNG>
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
        } else AState { rng ->
            nonNegativeIntLessThan(n).myRun(rng)
        }
    }
}