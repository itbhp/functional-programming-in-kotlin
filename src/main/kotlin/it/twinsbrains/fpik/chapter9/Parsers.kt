package it.twinsbrains.fpik.chapter9

import arrow.core.Either
import arrow.core.Right
import it.twinsbrains.fpik.chapter8.Checkers.forAll
import it.twinsbrains.fpik.chapter8.Gen
import it.twinsbrains.fpik.chapter8.Gen.Companion.combine
import it.twinsbrains.fpik.chapter8.Prop
import java.util.regex.Pattern

typealias Parser<A> = (Location) -> Result<A>

sealed class Result<out A>
data class Success<out A>(val a: A, val consumed: Int) : Result<A>()
data class Failure(val get: ParseError) : Result<Nothing>()

interface Parsers<PE> {

  fun <A> run(p: Parser<A>, input: String): Either<PE, A>

  fun string(s: String): Parser<String>

  fun <A> pure(a: A): Parser<A>

  fun regexp(s: String): Parser<String>

  fun <A> Parser<A>.slice(): Parser<String>

  fun <A> tag(msg: String, p: Parser<A>): Parser<A>

  fun <A> scope(msg: String, p: Parser<A>): Parser<A>

  fun <A, B> Parser<A>.flatMap(f: (A) -> Parser<B>): Parser<B>

  fun <A> attempt(p: Parser<A>): Parser<A>

  infix fun <A> Parser<A>.or(a2: () -> Parser<A>): Parser<A>

  fun <A> furthest(pa: Parser<A>): Parser<A>

  fun <A> latest(pa: Parser<A>): Parser<A>

  infix fun <A> Parser<A>.skipR(ps: Parser<String>): Parser<A>

  infix fun <B> Parser<String>.skipL(pb: Parser<B>): Parser<B>

  infix fun <A> Parser<A>.sep(p2: Parser<String>): Parser<List<A>>

  fun <A> surround(start: Parser<String>, stop: Parser<String>, p: Parser<A>): Parser<A>

  fun <A> listOfN(n: Int, p: Parser<A>): Parser<List<A>> =
    if (n < 0) {
      pure(emptyList())
    } else {
      map2(p, { listOfN(n - 1, p) }, { a, la -> listOf(a) + la })
    }

  fun <A> Parser<A>.many(): Parser<List<A>> = many1(this) or { pure(emptyList()) }

  fun <A, B> Parser<A>.map(f: (A) -> B): Parser<B> = this.flatMap { a -> pure(f(a)) }

  infix fun <A, B> Parser<A>.product(pb: () -> Parser<B>): Parser<Pair<A, B>> = this.flatMap { a ->
    pb().map { b -> a to b }
  }

  fun <A, B, C> map2(
    pa: Parser<A>,
    pb: () -> Parser<B>,
    f: (A, B) -> C
  ): Parser<C> = (pa product pb).map { (a, b) -> f(a, b) }

  fun <A> many1(p: Parser<A>): Parser<List<A>> = map2(p, { p.many() }, { a, la -> listOf(a) + la })

  infix fun String.or(other: String): Parser<String> =
    pure(this) or { pure(other) }

  fun repeatedChar(aChar: Char): Parser<Int> =
    regexp("\\d{1}")
      .flatMap { c ->
        val size = c.toInt()
        listOfN(size, pure(aChar)).map { size }
      }
}

data class ParseError(val stack: List<Pair<Location, String>>)

data class Location(val input: String, val offset: Int = 0) {
  private val slice by lazy { input.slice(0..offset + 1) }
  val line by lazy { slice.count { it == '\n' } + 1 }
  val column by lazy {
    when (val n = slice.lastIndexOf('\n')) {
      -1 -> offset + 1
      else -> offset - n
    }
  }
}

fun errorLocation(e: ParseError): Location = TODO()
fun errorMessage(e: ParseError): String = TODO()

abstract class Laws : Parsers<ParseError> {
  private fun <A> equal(
    p1: Parser<A>,
    p2: Parser<A>,
    i: Gen<String>
  ): Prop =
    forAll(i) { s -> run(p1, s) == run(p2, s) }

  fun <A> mapLaw(p: Parser<A>, i: Gen<String>): Prop =
    equal(p, p.map { a -> a }, i)

  fun pureLaw(gc: Gen<String>): Prop = forAll(gc combine gc) { (s, a) -> run(pure(s), a) == Right(s) }

  fun <A> orAssociativity(pa: Parser<A>, pb: Parser<A>, pc: Parser<A>, gc: Gen<String>): Prop =
    equal(pa or { pb or { pc } }, (pa or { pb }) or { pc }, gc)

  fun <A, B, C> productAssociativity(pa: Parser<A>, pb: Parser<B>, pc: Parser<C>, gc: Gen<String>): Prop =
    equal(
      (pa product { (pb product { pc }) }).map(::unbiasR),
      ((pa product { pb }) product { pc }).map(::unbiasL),
      gc
    )

  fun <A, B, C, D> mapProductLaw(pa: Parser<A>, pb: Parser<B>, f: (A) -> C, g: (B) -> D, gc: Gen<String>): Prop =
    equal(pa.map(f) product { pb.map(g) }, (pa product { pb }).map { (a, b) -> f(a) to g(b) }, gc)


  private fun <A, B, C> unbiasL(p: Pair<Pair<A, B>, C>): Triple<A, B, C> =
    Triple(p.first.first, p.first.second, p.second)

  private fun <A, B, C> unbiasR(p: Pair<A, Pair<B, C>>): Triple<A, B, C> =
    Triple(p.first, p.second.first, p.second.second)

}

sealed class JSON {
  object JNull : JSON()
  data class JNumber(val get: Double) : JSON()
  data class JString(val get: String) : JSON()
  data class JBoolean(val get: Boolean) : JSON()
  data class JArray(val get: List<JSON>) : JSON()
  data class JObject(val get: Map<String, JSON>) : JSON()
}

interface JsonParser : Parsers<ParseError> {

  private val JSON.parser: Parser<JSON>
    get() = pure(this)

  private val String.rp: Parser<String>
    get() = regexp(this)

  private val String.sp: Parser<String>
    get() = pure(this)

  private fun thru(s: String): Parser<String> =
    ".*?${Pattern.quote(s)}".rp

  private fun quoted(): Parser<String> =
    "\"".sp skipL thru("\"").map { it.dropLast(1) }

  private fun doubleString(): Parser<String> =
    "[-+]?([0-9]*\\.)?[0-9]+([eE][-+]?[0-9]+)?".rp

  private fun doubleJson(): Parser<JSON.JNumber> = doubleString().map { JSON.JNumber(it.toDouble()) }

  private fun literals(): Parser<JSON> =
    JSON.JNull.parser or
      { doubleJson() } or
      { JSON.JBoolean(true).parser } or
      { JSON.JBoolean(false).parser } or
      { quoted().map { JSON.JString(it) } }

  private fun value(): Parser<JSON> = literals() or { obj() or { array() } }

  private fun array(): Parser<JSON.JArray> =
    surround("[".sp, "]".sp,
      (value() sep ",".sp).map { vs -> JSON.JArray(vs) })

  private fun keyval(): Parser<Pair<String, JSON>> =
    quoted() product { (":".sp skipL value()) }

  private fun obj(): Parser<JSON> =
    surround("{".sp, "}".sp,
      (keyval() sep ",".sp).map { kvs -> JSON.JObject(kvs.toMap()) })

  private fun <A> root(p: Parser<A>): Parser<A> = p skipR eof()

  private fun whitespace(): Parser<String> = """\s*""".rp

  private fun eof(): Parser<String> = """\z""".rp

  fun jsonParser(): Parser<JSON> =
    root(whitespace() skipL (obj() or { array() }))

}