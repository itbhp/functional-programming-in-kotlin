package it.twinsbrains.fpik.chapter7

import arrow.core.extensions.list.foldable.firstOption
import arrow.core.getOrElse
import java.util.concurrent.ExecutorService
import java.util.concurrent.Future

typealias Par<A> = (ExecutorService) -> Future<A>

interface ParSyntax {
  fun <A, B, C> map2(a: Par<A>, b: Par<B>, f: (A, B) -> C): Par<C> = TODO()
  fun <A> unit(a: A): Par<A> = TODO()
  fun <A> fork(a: () -> Par<A>): Par<A> = TODO()
  fun <A> lazyUnit(a: () -> A): Par<A> =
    fork { unit(a()) }
}

fun <A> run(es: ExecutorService, a: Par<A>): Future<A> = a(es)

object ParExamples : ParSyntax {
  fun sum(ints: List<Int>): Par<Int> =
    if (ints.size <= 1)
      unit(ints.firstOption().getOrElse { 0 })
    else {
      val (l, r) = ints.chunked(ints.size / 2)
      map2(fork { sum(l) }, fork { sum(r) }) { lx: Int, rx: Int -> lx + rx }
    }
}