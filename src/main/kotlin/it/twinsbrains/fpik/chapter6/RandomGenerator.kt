package it.twinsbrains.fpik.chapter6

import it.twinsbrains.fpik.chapter3.Cons
import it.twinsbrains.fpik.chapter3.List

interface RNG {
    fun nextInt(): Pair<Int, RNG>
}

class LinearCongruentialGenerator(private val seed: Long) : RNG {
    override fun nextInt(): Pair<Int, RNG> {
        val newSeed =
            (seed * 0x5DEECE66DL + 0xBL) and
                    0xFFFFFFFFFFFFL
        val nextRNG = LinearCongruentialGenerator(newSeed)
        val n = (newSeed ushr 16).toInt()
        return Pair(n, nextRNG)
    }
}

object Randoms {

    fun nonNegativeInt(rng: RNG): Pair<Int, RNG> {
        val (num, nextRng) = rng.nextInt()
        if (num < 0) {
            return (-(num + 1) to nextRng)
        } else {
            return num to nextRng
        }
    }

    fun double(rng: RNG): Pair<Double, RNG> {
        val (num, nextRng) = nonNegativeInt(rng)
        return num.toDouble() / Int.MAX_VALUE to nextRng
    }

    fun ints(count: Int, rng: RNG): Pair<List<Int>, RNG> {
        tailrec fun loop(n: Int, nRNG: RNG, l: List<Int>): Pair<List<Int>, RNG> {
            return if (n <= 0) {
                l to nRNG
            } else {
                val (i, rng1) = nRNG.nextInt()
                loop(n - 1, rng1, Cons(i, l))
            }
        }
        return loop(count, rng, List.empty())
    }

}
