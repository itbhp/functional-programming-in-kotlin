package it.twinsbrains.chapter3

sealed class List<out A> {
  companion object {
    fun <A> of(vararg aa: A): List<A> {
      val tail = aa.sliceArray(1 until aa.size)
      return if (aa.isEmpty()) Nil else Cons(aa[0], of(*tail))
    }

    fun sum(ints: List<Int>): Int =
      when (ints) {
        is Nil -> 0
        is Cons -> ints.head + sum(ints.tail)
      }

    fun product(doubles: List<Double>): Double =
      when (doubles) {
        is Nil -> 1.0
        is Cons -> if (doubles.head == 0.0) 0.0
        else doubles.head * product(doubles.tail)
      }


    fun <A> tail(xs: List<A>): List<A> =
      when (xs) {
        is Nil -> Nil
        is Cons -> xs.tail
      }
  }
}

object Nil : List<Nothing>()
data class Cons<out A>(
  val head: A,
  val tail: List<A>
) : List<A>()
