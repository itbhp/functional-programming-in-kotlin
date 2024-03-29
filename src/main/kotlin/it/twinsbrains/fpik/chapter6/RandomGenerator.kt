package it.twinsbrains.fpik.chapter6

import it.twinsbrains.fpik.chapter3.Cons
import it.twinsbrains.fpik.chapter3.List

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
    return if (num < 0) {
      (-(num + 1) to nextRng)
    } else {
      num to nextRng
    }
  }

  fun double(rng: RNG): Pair<Double, RNG> {
    val (num, nextRng) = nonNegativeInt(rng)
    return num.toDouble() / Int.MAX_VALUE to nextRng
  }

  fun ints(count: Int, rng: RNG): Pair<List<Int>, RNG> =
    (1..count)
      .fold(List.empty<Int>() to rng) { (l, r), _ ->
        val (num, nRng) = nonNegativeInt(r)
        Cons(num, l) to nRng
      }
}