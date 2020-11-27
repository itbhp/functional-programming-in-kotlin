package it.twinsbrains.chapter4

sealed class Either<out E, out A> {
  fun <A> catches(a: () -> A): Either<Exception, A> = try {
    Right(a())
  } catch (e: Exception) {
    Left(e)
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
