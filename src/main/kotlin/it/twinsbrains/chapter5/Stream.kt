package it.twinsbrains.chapter5

import it.twinsbrains.chapter4.None
import it.twinsbrains.chapter4.Option
import it.twinsbrains.chapter4.Some

sealed class Stream<out A> {
  companion object {
    fun <A> Stream<A>.headOption(): Option<A> = when (this) {
      is Empty -> None
      is Cons -> Some(head())
    }

    fun <A> cons(hd: () -> A, tl: () -> Stream<A>): Stream<A> {
      val head: A by lazy(hd)
      val tail: Stream<A> by lazy(tl)
      return Cons({ head }, { tail })
    }

    fun <A> empty(): Stream<A> = Empty
  }
}

data class Cons<out A>(
  val head: () -> A,
  val tail: () -> Stream<A>
) : Stream<A>()

object Empty : Stream<Nothing>()