package it.twinsbrains.fpik.chapter6

class Fixed(private val n: Int) : RNG {
  override fun nextInt(): Pair<Int, RNG> {
    return n to this
  }
}