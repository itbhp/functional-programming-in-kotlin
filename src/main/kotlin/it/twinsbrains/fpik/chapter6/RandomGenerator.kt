package it.twinsbrains.fpik.chapter6

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

    fun intDouble(rng: RNG): Pair<Pair<Int, Double>, RNG> {
        val (i, r) = nonNegativeInt(rng)
        return (i to i.toDouble()) to r
    }

    fun doubleInt(rng: RNG): Pair<Pair<Double, Int>, RNG> {
        val (d, r) = double(rng)
        return (d to d.toInt()) to r
    }

    fun double3(rng: RNG): Pair<Triple<Double, Double, Double>, RNG> {
        val (v1, rng1) = double(rng)
        val (v2, rng2) = double(rng1)
        val (v3, rng3) = double(rng2)

        return Triple(v1, v2, v3) to rng3
    }

}
