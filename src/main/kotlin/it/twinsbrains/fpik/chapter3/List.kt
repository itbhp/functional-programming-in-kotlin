package it.twinsbrains.fpik.chapter3

sealed class List<out A> {
  companion object {

    fun <A> empty(): List<A> = Nil

    fun <A> of(vararg aa: A): List<A> {
      val tail = aa.sliceArray(1 until aa.size)
      return if (aa.isEmpty()) Nil else Cons(aa[0], of(*tail))
    }

    fun sum(ints: List<Int>): Int =
      foldRight(ints, 0) { a, b -> a + b }

    fun product(doubles: List<Double>): Double =
      foldRight(doubles, 1.0) { a, b -> a * b }

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
      foldRight(a1, a2) { e, l -> Cons(e, l) }

    fun <A> init(l: List<A>): List<A> = when (l) {
      is Nil -> throw IllegalArgumentException("init called on empty list")
      is Cons -> when (l.tail) {
        is Nil -> Nil
        is Cons -> Cons(l.head, init(l.tail))
      }
    }

    fun <A> reverse(l: List<A>): List<A> =
      foldLeft(
        l,
        empty()
      ) { acc: List<A>, e: A -> Cons(e, acc) }

    fun <A, B> foldRight(xs: List<A>, z: B, f: (A, B) -> B): B =
      foldLeft(xs, { it }, { acc: (B) -> B, e: A -> { b -> acc(f(e, b)) } })(z)

    fun <A> length(xs: List<A>): Int = foldRight(xs, 0, { _, acc -> acc + 1 })

    private tailrec fun <A, B> foldLeft(xs: List<A>, z: B, f: (B, A) -> B): B =
      when (xs) {
        is Nil -> z
        is Cons -> foldLeft(xs.tail, f(z, xs.head), f)
      }

    fun <A> concatenate(ls: List<List<A>>): List<A> =
      foldRight(ls, empty()) { l, acc -> append(l, acc) }

    fun addOne(xs: List<Int>): List<Int> =
      map(xs) { e -> e + 1 }

    private fun <A, B> map(xs: List<A>, f: (A) -> B): List<B> =
      foldRight(xs, empty()) { e, l -> Cons(f(e), l) }

    fun <A> filter(xs: List<A>, f: (A) -> Boolean): List<A> =
      flatMap(xs) { e -> if (f(e)) of(e) else empty() }

    fun <A, B> flatMap(xa: List<A>, f: (A) -> List<B>): List<B> =
      concatenate(map(xa, f))

    fun <A, B, C> zipWith(xs: List<A>, ys: List<B>, f: (A, B) -> C): List<C> =
      when (xs) {
        is Nil -> Nil
        is Cons ->
          when (ys) {
            is Nil -> Nil
            is Cons -> Cons(f(xs.head, ys.head), zipWith(xs.tail, ys.tail, f))
          }
      }

    tailrec fun <A> hasSubsequence(xs: List<A>, sub: List<A>): Boolean =
      when (xs) {
        is Nil -> Nil == sub
        is Cons ->
          when (sub) {
            is Nil -> true
            is Cons -> if (xs.head == sub.head) {
              hasSubsequence(xs.tail, sub.tail)
            } else {
              hasSubsequence(xs.tail, sub)
            }
          }
      }
  }
}

object Nil : List<Nothing>()
data class Cons<out A>(
  val head: A,
  val tail: List<A>
) : List<A>()