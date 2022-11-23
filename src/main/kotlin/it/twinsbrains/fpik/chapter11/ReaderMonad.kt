package it.twinsbrains.fpik.chapter11

import arrow.Kind

class ForReader
typealias ReaderPartialOf<R> = Kind<ForReader, R>
typealias ReaderOf<R, A> = Kind<ReaderPartialOf<R>, A>

interface ReaderMonad<R> : Monad<ReaderPartialOf<R>> {
  override fun <A> unit(a: A): ReaderOf<R, A>
}

fun <R, A> ReaderOf<R, A>.fix(): Reader<R, A> = this as Reader<R, A>

data class Reader<R, A>(
  val run: (R) -> A
) : ReaderOf<R, A> {
  companion object {
    fun <R, A> unit(a: A): Reader<R, A> = Reader { _: R -> a }

    fun <R, A> monad(): ReaderMonad<R> = object : ReaderMonad<R> {
      override fun <A, B> flatMap(
        fa: ReaderOf<R, A>,
        f: (A) -> ReaderOf<R, B>
      ): ReaderOf<R, B> = fa.fix().flatMap { f(it).fix() }

      override fun <A> unit(a: A): ReaderOf<R, A> = Companion.unit(a)
    }
  }

  fun <B> flatMap(f: (A) -> Reader<R, B>): Reader<R, B> =
    Reader { r: R -> f(this.run(r)).run(r) }

  fun <B> map(f: (A) -> B): Reader<R, B> =
    flatMap { a -> unit(f(a)) }
}