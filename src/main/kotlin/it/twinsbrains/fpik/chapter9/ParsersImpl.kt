package it.twinsbrains.fpik.chapter9

abstract class ParsersImpl : Parsers<ParseError> {
  override fun string(s: String): Parser<String> =
    { l: Location ->
      if (l.input.startsWith(s))
        Success(s, 1)
      else Failure(Location(l.input).toError("Expected: $s"))
    }

  private fun Location.toError(msg: String) =
    ParseError(listOf(Pair(this, msg)))
}