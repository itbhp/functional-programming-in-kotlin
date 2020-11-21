package it.twinsbrains.chapter3

sealed class List<out A> {
  companion object {

    fun <A> empty(): List<A> = Nil

    fun <A> of(vararg aa: A): List<A> {
      val tail = aa.sliceArray(1 until aa.size)
      return if (aa.isEmpty()) Nil else Cons(aa[0], of(*tail))
    }

    fun sum(ints: List<Int>): Int =
      foldRight(ints, 0, { a, b -> a + b })

    fun product(doubles: List<Double>): Double =
      foldRight(doubles, 1.0, { a, b -> a * b })

    fun <A> tail(xs: List<A>): List<A> =
      when (xs) {
        is Nil -> Nil
        is Cons -> xs.tail
      }

    fun <A> drop(l: List<A>, n: Int): List<A> =
      when (l) {
        is Nil -> Nil
        is Cons -> when {
          n > 0 -> drop(l.tail, n - 1)
          else -> l
        }
      }

    fun <A> dropWhile(l: List<A>, f: (a: A) -> Boolean): List<A> =
      when (l) {
        is Nil -> Nil
        is Cons -> when {
          f(l.head) -> dropWhile(l.tail, f)
          else -> l
        }
      }

    fun <A> append(a1: List<A>, a2: List<A>): List<A> =
      when (a1) {
        is Nil -> a2
        is Cons -> Cons(a1.head, append(a1.tail, a2))
      }

    fun <A> init(l: List<A>): List<A> = when (l) {
      is Nil -> throw IllegalArgumentException("init called on empty list")
      is Cons -> when (l.tail) {
        is Nil -> Nil
        is Cons -> Cons(l.head, init(l.tail))
      }
    }

    fun <A, B> foldRight(xs: List<A>, z: B, f: (A, B) -> B): B =
      when (xs) {
        is Nil -> z
        is Cons -> f(xs.head, foldRight(xs.tail, z, f))
      }

    fun <A> length(xs: List<A>): Int = foldRight(xs, 0, { _, acc -> acc + 1 })

    tailrec fun <A, B> foldLeft(xs: List<A>, z: B, f: (B, A) -> B): B =
      when (xs) {
        is Nil -> z
        is Cons -> foldLeft(xs.tail, f(z, xs.head), f)
      }
  }
}

object Nil : List<Nothing>()
data class Cons<out A>(
  val head: A,
  val tail: List<A>
) : List<A>()
