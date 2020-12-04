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
