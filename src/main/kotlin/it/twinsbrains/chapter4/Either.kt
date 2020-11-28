package it.twinsbrains.chapter4

import it.twinsbrains.chapter3.Cons
import it.twinsbrains.chapter3.List

sealed class Either<out E, out A> {

  companion object {
    fun <E, A> right(a: A): Either<E, A> = Right(a)
    fun <E, A> left(e: E): Either<E, A> = Left(e)

    fun <A> catches(a: () -> A): Either<Exception, A> =
      try {
        Right(a())
      } catch (e: Exception) {
        Left(e)
      }

    fun <E, A, B> lift(f: (A) -> B): (Either<E, A>) -> Either<E, B> = { oa -> oa.map(f) }
    fun <E, A, B, C> lift2(f: (A, B) -> C): (Either<E, A>, Either<E, B>) -> Either<E, C> =
      { optA, optB ->
        optA.flatMap { a ->
          optB.map { b ->
            f(a, b)
          }
        }
      }

    fun <E, A, B, C> map2(a: Either<E, A>, b: Either<E, B>, f: (A, B) -> C): Either<E, C> = lift2<E, A, B, C>(f)(a, b)

    fun <E, A> sequence(xs: List<Either<E, A>>): Either<E, List<A>> =
      traverse(xs) { it }

    fun <E, A, B> traverse(
      xa: List<A>,
      f: (A) -> Either<E, B>
    ): Either<E, List<B>> =
      List.foldRight(
        xa,
        right(List.empty())
      )
      { a, optListB -> map2(f(a), optListB) { b, l -> Cons(b, l) } }
  }
}

data class Left<out E>(val value: E) : Either<E, Nothing>()
data class Right<out A>(val value: A) : Either<Nothing, A>()

fun <E, A, B> Either<E, A>.map(f: (A) -> B): Either<E, B> =
  this.flatMap { a -> Right(f(a)) }

fun <E, A, B> Either<E, A>.flatMap(f: (A) -> Either<E, B>): Either<E, B> =
  when (this) {
    is Left -> this
    is Right -> f(this.value)
  }

fun <E, A> Either<E, A>.orElse(ob: () -> Either<E, A>): Either<E, A> =
  when (this) {
    is Left -> ob()
    is Right -> this
  }
