package it.twinsbrains.fpik.chapter9

import arrow.core.Left
import arrow.core.Right

abstract class ParsersImpl : Parsers<ParseError> {
  override fun string(s: String): Parser<String> =
    { input: String ->
      if (input.startsWith(s))
        Right(s)
      else Left(Location(input).toError("Expected: $s"))
    }

  private fun Location.toError(msg: String) =
    ParseError(listOf(Pair(this, msg)))
}