package it.twinsbrains.fpik.chapter9

import arrow.core.Either
import arrow.core.filterOrElse

typealias Option<A> = Either<Unit, A>
typealias Some<A> = Either.Right<A>
typealias None = Either.Left<Unit>

fun <A> some(a: A): Option<A> = Either.right(a)
fun <A> none(): Option<A> = Either.left(Unit)

fun <A> A?.toOption(): Option<A> =
  if (this != null) {
    some(this)
  } else {
    none()
  }

val <A> Either.Right<A>.t: A
  get() = this.b

fun <A> Option<A>.filter(p: (A) -> Boolean) = this.filterOrElse({ p(it) }, {})

abstract class ParsersImpl : Parsers<ParseError> {
  override fun string(s: String): Parser<String> =
    { state: State ->
      when (val idx = firstNonMatchingIndex(state.input, s, state.offset)) {
        is None -> Success(s, s.length)
        is Some ->
          Failure(
            state.advanceBy(idx.t).toError("'$s'")
          )
      }
    }

  private fun firstNonMatchingIndex(
    s1: String,
    s2: String,
    offset: Int
  ): Option<Int> {
    var i = 0
    while (i < s1.length && i < s2.length) {
      if (s1[i + offset] != s2[i]) {
        return some(i)
      } else {
        i += 1
      }
    }
    return if (s1.length - offset >= s2.length) {
      none()
    } else {
      some(s1.length - offset)
    }
  }

  private fun State.advanceBy(i: Int): State =
    this.copy(offset = this.offset + i)

  override fun regex(r: String): Parser<String> = { state: State ->
    when (val prefix = state.input.findPrefixOf(r.toRegex())) {
      is Some ->
        Success(prefix.t.value, prefix.t.value.length)

      is None ->
        Failure(state.toError("regex ${r.toRegex()}"))
    }
  }

  private fun String.findPrefixOf(r: Regex): Option<MatchResult> =
    r.find(this).toOption().filter { it.range.first == 0 }

  override fun <A> succeed(a: A): Parser<A> = { Success(a, 0) }

  override fun <A> slice(p: Parser<A>): Parser<String> =
    { state: State ->
      when (val result = p(state)) {
        is Success ->
          Success(state.slice(result.consumed), result.consumed)

        is Failure -> result
      }
    }

  private fun State.slice(n: Int): String = this.input.substring(this.offset..this.offset + n)

  private fun Location.toError(msg: String) =
    ParseError(listOf(Pair(this, msg)))
}