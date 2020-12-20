package it.twinsbrains.fpik.chapter6

class FixedRNG(private val n: Int) : RNG {
  override fun nextInt(): Pair<Int, RNG> {
    return n to this
  }
}