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
    TimedMap2Future(af, bf, f)
  }

  fun <A> unit(a: A): Par<A> = { _: ExecutorService -> UnitFuture(a) }

  fun <A> fork(a: () -> Par<A>): Par<A> = { es: ExecutorService ->
    es.submit(Callable { a()(es).get() })
  }

  fun <A> lazyUnit(a: () -> A): Par<A> = fork { unit(a()) }

  fun <A> run(es: ExecutorService, a: Par<A>): Future<A> = a(es)

  fun <A, B> asyncF(f: (A) -> B): (A) -> Par<B> = { a ->
    lazyUnit { f(a) }
  }
}

data class UnitFuture<A>(val a: A) : Future<A> {
  override fun get(): A = a
  override fun get(timeout: Long, timeUnit: TimeUnit): A = a
  override fun cancel(evenIfRunning: Boolean): Boolean = false
  override fun isDone(): Boolean = true
  override fun isCancelled(): Boolean = false
}

data class TimedMap2Future<A, B, C>(val pa: Future<A>, val pb: Future<B>, val f: (A, B) -> C) : Future<C> {
  override fun get(to: Long, tu: TimeUnit): C {
    val timeoutMillis = TimeUnit.MILLISECONDS.convert(to, tu)
    val start = System.currentTimeMillis()
    val a = pa.get(to, tu)
    val duration = System.currentTimeMillis() - start
    val remainder = timeoutMillis - duration
    val b = pb.get(remainder, TimeUnit.MILLISECONDS)
    return f(a, b)
  }

  override fun get(): C {
    return f(pa.get(), pb.get())
  }

  override fun cancel(mayInterruptIfRunning: Boolean): Boolean {
    return pa.cancel(mayInterruptIfRunning) && pa.cancel(mayInterruptIfRunning)
  }

  override fun isCancelled(): Boolean {
    return pa.isCancelled && pb.isCancelled
  }

  override fun isDone(): Boolean {
    return pa.isDone && pb.isDone
  }

}


object ParExamples {
  fun sum(ints: List<Int>): Par<Int> =
    if (ints.size <= 1)
      unit(ints.firstOption().getOrElse { 0 })
    else {
      val (l, r) = ints.splitAt(ints.size / 2)
      map2(fork { sum(l) }, fork { sum(r) }) { lx: Int, rx: Int -> lx + rx }
    }
}

fun List<Int>.splitAt(i: Int) =
  this.subList(0, i) to this.subList(i, this.size)