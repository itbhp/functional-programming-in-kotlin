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
  }
}

data class Cons<out A>(
  val head: () -> A,
  val tail: () -> Stream<A>
) : Stream<A>()

object Empty : Stream<Nothing>()