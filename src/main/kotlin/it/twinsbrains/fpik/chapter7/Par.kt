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

  fun <A, B, C, D> map3(a: Par<A>, b: Par<B>, c: Par<C>, f: (A, B, C) -> D): Par<D> =
    map2(a, map2(b, c, { vB, vC -> { vA: A -> f(vA, vB, vC) } }), { vA, mapping -> mapping(vA) })

  fun <A, B, C> map2(a: Par<A>, b: Par<B>, f: (A, B) -> C): Par<C> = { es: ExecutorService ->
    val af: Future<A> = a(es)
    val bf: Future<B> = b(es)
    TimedMap2Future(af, bf, f)
  }

  fun <A> unit(a: A): Par<A> = { _: ExecutorService -> UnitFuture(a) }

  fun <A> fork(a: () -> Par<A>): Par<A> = { es: ExecutorService ->
    es.submit(Callable {
      a()(es).get()
    })
  }

  fun <A> delay(pa: () -> Par<A>): Par<A> =
    { es -> pa()(es) }

  fun <A> lazyUnit(a: () -> A): Par<A> = fork { unit(a()) }

  fun <A> run(es: ExecutorService, a: Par<A>): Future<A> = a(es)

  fun <A, B> asyncF(f: (A) -> B): (A) -> Par<B> = { a ->
    lazyUnit { f(a) }
  }

  fun sortPar(parList: Par<List<Int>>): Par<List<Int>> =
    map(parList) { it.sorted() }

  fun <A, B> map(pa: Par<A>, f: (A) -> B): Par<B> =
    map2(pa, unit(Unit), { a, _ -> f(a) })

  fun <A, B> parMap(
    ps: List<A>,
    f: (A) -> B
  ): Par<List<B>> = fork {
    val fbs: List<Par<B>> = ps.map(asyncF(f))
    sequence(fbs)
  }

  fun <A> sequence(ps: List<Par<A>>): Par<List<A>> =
    when {
      ps.isEmpty() -> unit(Nil)
      ps.size == 1 -> map(ps.head) { listOf(it) }
      else -> {
        val (l, r) = ps.splitAt(ps.size / 2)
        map2(sequence(l), sequence(r)) { la, lb -> la + lb }
      }
    }

  fun <A> parFilter(
    ps: List<A>,
    f: (A) -> Boolean
  ): Par<List<A>> =
    when {
      ps.isEmpty() -> unit(Nil)
      ps.size == 1 -> if (f(ps.head)) {
        unit(listOf(ps.head))
      } else unit(emptyList())
      else -> {
        val (l, r) = ps.splitAt(ps.size / 2)
        map2(parFilter(l, f), parFilter(r, f)) { la, lb -> la + lb }
      }
    }

  fun <A> choice(cond: Par<Boolean>, t: Par<A>, f: Par<A>): Par<A> =
    choiceN(map(cond, { b -> if (b) 0 else 1 }), listOf(t, f))

  fun <A> choiceN(n: Par<Int>, choices: List<Par<A>>): Par<A> =
    { es: ExecutorService ->
      run(es, choices[run(es, n).get()])
    }

  fun <K, V> choiceMap(
    key: Par<K>,
    choices: Map<K, Par<V>>
  ): Par<V> =
    { es: ExecutorService ->
      val keyV: K = run(es, key).get()
      run(es, choices.getValue(keyV))
    }

  fun <A, B> chooser(pa: Par<A>, choices: (A) -> Par<B>): Par<B> =
    { es: ExecutorService ->
      val vA: A = run(es, pa).get()
      run(es, choices(vA))
    }

}

val <T> List<T>.head: T
  get() = first()
val <T> List<T>.tail: List<T>
  get() = this.drop(1)

val Nil = listOf<Nothing>()

fun <T> List<T>.splitAt(i: Int) =
  this.subList(0, i) to this.subList(i, this.size)


data class UnitFuture<A>(
  val a: A
) : Future<A> {
  override fun get(): A = a
  override fun get(timeout: Long, timeUnit: TimeUnit): A = a
  override fun cancel(evenIfRunning: Boolean): Boolean = false
  override fun isDone(): Boolean = true
  override fun isCancelled(): Boolean = false
}

data class TimedMap2Future<A, B, C>(
  val pa: Future<A>,
  val pb: Future<B>,
  val f: (A, B) -> C
) : Future<C> {
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
