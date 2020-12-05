package it.twinsbrains.fpik.chapter7

import arrow.core.extensions.list.foldable.firstOption
import arrow.core.getOrElse
import it.twinsbrains.fpik.chapter7.Pars.fork
import it.twinsbrains.fpik.chapter7.Pars.map2
import it.twinsbrains.fpik.chapter7.Pars.unit
import java.util.concurrent.Callable
import java.util.concurrent.ExecutorService
import java.util.concurrent.Future
import java.util.concurrent.TimeUnit

typealias Par<A> = (ExecutorService) -> Future<A>

object Pars {
  fun <A, B, C> map2(a: Par<A>, b: Par<B>, f: (A, B) -> C): Par<C> = { es: ExecutorService ->
    val af: Future<A> = a(es)
    val bf: Future<B> = b(es)
    UnitFuture(f(af.get(), bf.get()))
  }

  fun <A> unit(a: A): Par<A> = { _: ExecutorService -> UnitFuture(a) }

  fun <A> fork(a: () -> Par<A>): Par<A> = { es: ExecutorService ->
    es.submit(Callable { a()(es).get() })
  }

  fun <A> run(es: ExecutorService, a: Par<A>): Future<A> = a(es)
}

data class UnitFuture<A>(val a: A) : Future<A> {
  override fun get(): A = a
  override fun get(timeout: Long, timeUnit: TimeUnit): A = a
  override fun cancel(evenIfRunning: Boolean): Boolean = false
  override fun isDone(): Boolean = true
  override fun isCancelled(): Boolean = false
}


object ParExamples {
  fun sum(ints: List<Int>): Par<Int> =
    if (ints.size <= 1)
      unit(ints.firstOption().getOrElse { 0 })
    else {
      val (l, r) = ints.chunked(ints.size / 2)
      map2(fork { sum(l) }, fork { sum(r) }) { lx: Int, rx: Int -> lx + rx }
    }
}