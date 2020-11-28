package it.twinsbrains.chapter5

import it.twinsbrains.chapter3.List
import it.twinsbrains.chapter4.Option
import it.twinsbrains.chapter4.Option.Companion.none
import it.twinsbrains.chapter4.Option.Companion.some
import it.twinsbrains.chapter3.Cons as consL

sealed class Stream<out A> {
  companion object {

    fun <A> Stream<A>.filter(p: (A) -> Boolean): Stream<A> =
      foldRight({ empty() }) { a, s -> if (p(a)) cons({ a }, s) else s() }

    fun <A, B> Stream<A>.map(f: (A) -> B): Stream<B> =
      foldRight({ empty() }) { a, s -> cons({ f(a) }, s) }

    fun <A> Stream<A>.forAll(p: (A) -> Boolean): Boolean =
      foldRight({ false }, { a, b -> p(a) && b() })

    fun <A, B> Stream<A>.foldRight(
      z: () -> B,
      f: (A, () -> B) -> B
    ): B =
      when (this) {
        is Cons -> f(this.head()) { tail().foldRight(z, f) }
        else -> z()
      }

    fun <A> Stream<A>.exists(p: (A) -> Boolean): Boolean =
      foldRight({ false }, { a, b -> p(a) || b() })

    fun <A> Stream<A>.takeWhile(p: (A) -> Boolean): Stream<A> =
      foldRight({ empty() }, { a, thunkAcc -> if (p(a)) cons({ a }, thunkAcc) else thunkAcc() })

    fun <A> Stream<A>.take(n: Int): Stream<A> =
      if (n <= 0)
        empty()
      else
        when (this) {
          is Empty -> Empty
          is Cons -> cons(this.head, { this.tail().take(n - 1) })
        }

    fun <A> Stream<A>.drop(n: Int): Stream<A> =
      if (n <= 0)
        this
      else
        when (this) {
          is Empty -> Empty
          is Cons -> this.tail().drop(n - 1)
        }

    fun <A> Stream<A>.toList(): List<A> {
      tailrec fun loop(stream: Stream<A>, acc: List<A>): List<A> {
        return when (stream) {
          is Empty -> acc
          is Cons -> loop(stream.tail(), consL(stream.head(), acc))
        }
      }
      return List.reverse(loop(this, List.empty()))
    }

    fun <A> Stream<A>.headOption(): Option<A> =
      foldRight({ none() }, { a, _ -> some(a) })

    fun <A> cons(hd: () -> A, tl: () -> Stream<A>): Stream<A> {
      val head: A by lazy(hd)
      val tail: Stream<A> by lazy(tl)
      return Cons({ head }, { tail })
    }

    fun <A> empty(): Stream<A> = Empty

    fun <A> of(vararg xs: A): Stream<A> =
      if (xs.isEmpty()) empty()
      else cons({ xs[0] },
        { of(*xs.sliceArray(1 until xs.size)) })
  }
}

data class Cons<out A>(
  val head: () -> A,
  val tail: () -> Stream<A>
) : Stream<A>()

object Empty : Stream<Nothing>()