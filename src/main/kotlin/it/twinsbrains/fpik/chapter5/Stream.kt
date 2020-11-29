package it.twinsbrains.fpik.chapter5

import it.twinsbrains.fpik.chapter3.List
import it.twinsbrains.fpik.chapter4.Option
import it.twinsbrains.fpik.chapter4.Option.Companion.none
import it.twinsbrains.fpik.chapter4.Option.Companion.some
import it.twinsbrains.fpik.chapter4.getOrElse
import it.twinsbrains.fpik.chapter4.map
import it.twinsbrains.fpik.chapter5.InfiniteStreams.unfold
import it.twinsbrains.fpik.chapter5.Stream.Companion.cons
import it.twinsbrains.fpik.chapter3.Cons as consL

sealed class Stream<out A> {
  companion object {

    fun <A> Stream<A>.startsWith(that: Stream<A>): Boolean =
      when(this){
        is Empty -> false
        is Cons -> when(that){
          is Empty -> true
          is Cons -> {
            val thisHead = this.head()
            val thatHead = that.head()
            if(thatHead == thisHead){
              this.tail().startsWith(that.tail())
            }else{
              false
            }
          }
        }
      }

    fun <A> Stream<A>.find(p: (A) -> Boolean): Option<A> =
      filter(p).headOption()

    fun <A> Stream<A>.append(another: Stream<A>): Stream<A> =
      foldRight({ another }) { a, acc -> cons({ a }, acc) }

    fun <A, B> Stream<A>.flatMap(f: (A) -> Stream<B>): Stream<B> =
      foldRight({ empty() }) { a, s -> f(a).append(s()) }

    fun <A> Stream<A>.filter(p: (A) -> Boolean): Stream<A> =
      foldRight({ empty() }) { a, s -> if (p(a)) cons({ a }, s) else s() }

    fun <A, B> Stream<A>.map(f: (A) -> B): Stream<B>
//      = foldRight({ empty() }) { a, s -> cons({ f(a) }, s) }
    {
      return unfold(this, { cur ->
        when (cur) {
          is Empty -> none()
          is Cons -> {
            val res = f(cur.head())
            some(res to cur.tail())
          }
        }
      })
    }

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

    fun <A> Stream<A>.takeWhile(p: (A) -> Boolean): Stream<A>
//       = foldRight({ empty() }, { a, thunkAcc -> if (p(a)) cons({ a }, thunkAcc) else thunkAcc() })
    {
      return unfold(this, { cur ->
        when (cur) {
          is Empty -> none()
          is Cons -> if (p(cur.head())) some(cur.head() to cur.tail()) else none()
        }
      })
    }

    fun <A> Stream<A>.take(n: Int): Stream<A> {
      data class TakeState(val cur: Stream<A>, val count: Int)
      return unfold(TakeState(this, n), { (cur, count) ->
        if (count <= 0) {
          none()
        } else {
          when (cur) {
            is Empty -> none()
            is Cons -> some(cur.head() to TakeState(cur.tail(), count - 1))
          }
        }
      })
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

object InfiniteStreams {

  fun ones(): Stream<Int> = constant(1)

  fun <A> constant(a: A): Stream<A> =
//    Stream.cons({ a }, { constant(a) })
    unfold(a, { n -> some(n to n) })

  fun from(n: Int): Stream<Int> =
//    Stream.cons({ n }, { from(n + 1) })
    unfold(n, { s -> some(s to s + 1) })

  fun fibs(): Stream<Int>
//  {
//    fun loop(beforePrev: Int, prev: Int): Stream<Int> =
//      Stream.cons({ beforePrev }, { loop(prev, prev + beforePrev) })
//    return loop(0, 1)
//  }
  {
    data class State(val beforePrev: Int, val prev: Int)
    return unfold(State(0, 1), { (value, previousValue) -> some(value to State(previousValue, value + previousValue)) })
  }


  fun <A, B> Stream<A>.zipAll(
    that: Stream<B>
  ): Stream<Pair<Option<A>, Option<B>>> {
    data class ZipState(val a: Stream<A>, val b: Stream<B>)
    return unfold(ZipState(this, that), { (curA, curB) ->
      when (curA) {
        is Cons -> {
          when (curB) {
            is Cons -> {
              val res = some(curA.head()) to some(curB.head())
              some(res to ZipState(curA.tail(), curB.tail()))
            }
            is Empty -> some((some(curA.head()) to none<B>()) to ZipState(curA.tail(), curB))
          }
        }
        is Empty -> none()
      }
    })
  }

  fun <A, B, C> Stream<A>.zipWith(
    that: Stream<B>,
    f: (A, B) -> C
  ): Stream<C> {
    data class State(val a: Stream<A>, val b: Stream<B>)
    return unfold(State(this, that), { (curA, curB) ->
      when (curA) {
        is Cons -> {
          when (curB) {
            is Cons -> {
              val res = f(curA.head(), curB.head())
              some(res to State(curA.tail(), curB.tail()))
            }
            is Empty -> none()
          }
        }
        is Empty -> none()
      }
    })
  }

  fun <A, S> unfold(z: S, f: (S) -> Option<Pair<A, S>>): Stream<A> {
    return f(z).map { (a, s) ->
      cons({ a }, { unfold(s, f) })
    }.getOrElse { Stream.empty() }
  }
}