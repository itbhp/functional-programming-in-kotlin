package it.twinsbrains.chapter4

import it.twinsbrains.chapter3.Cons
import it.twinsbrains.chapter3.List

sealed class Option<out A> {
  companion object {
    fun <A> none(): Option<A> = None
    fun <A> some(a: A): Option<A> = Some(a)

    fun <A, B> lift(f: (A) -> B): (Option<A>) -> Option<B> = { oa -> oa.map(f) }
    fun <A, B, C> lift2(f: (A, B) -> C): (Option<A>, Option<B>) -> Option<C> =
      { optA, optB ->
        optA.flatMap { a ->
          optB.map { b ->
            f(a, b)
          }
        }
      }

    fun <A> catches(a: () -> A): Option<A> = try {
      Some(a())
    } catch (e: Throwable) {
      None
    }

    fun <A, B, C> map2(a: Option<A>, b: Option<B>, f: (A, B) -> C): Option<C> = lift2(f)(a, b)

    fun <A> sequence(xs: List<Option<A>>): Option<List<A>> =
//      List.foldRight(
//        xs,
//        some(List.empty()),
////        { optA, acc -> optA.flatMap { a -> acc.map { l -> Cons(a, l) } } })
//        { optA, acc -> map2(optA, acc) { a, l -> Cons(a, l) } })
      traverse(xs) { it }

    fun <A, B> traverse(
      xa: List<A>,
      f: (A) -> Option<B>
    ): Option<List<B>> =
      List.foldRight(
        xa,
        some(List.empty())
      )
      { a, optListB -> map2(f(a), optListB) { b, l -> Cons(b, l) } }

  }
}

data class Some<out A>(val get: A) : Option<A>()
object None : Option<Nothing>()

fun <A, B> Option<A>.map(f: (A) -> B): Option<B> =
  when (this) {
    is None -> None
    is Some -> Some(f(this.get))
  }

fun <A, B> Option<A>.flatMap(f: (A) -> Option<B>): Option<B> =
  this.map(f).getOrElse { None }

fun <A> Option<A>.getOrElse(default: () -> A): A =
  when (this) {
    is None -> default()
    is Some -> this.get
  }

fun <A> Option<A>.orElse(ob: () -> Option<A>): Option<A> = this.map { Some(it) }.getOrElse { ob() }
fun <A> Option<A>.filter(f: (A) -> Boolean): Option<A> = this.flatMap { v -> if (f(v)) Some(v) else None }
