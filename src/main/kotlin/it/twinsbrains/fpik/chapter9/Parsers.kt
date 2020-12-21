package it.twinsbrains.fpik.chapter9

import arrow.core.Either
import arrow.core.Right
import it.twinsbrains.fpik.chapter8.Checkers.forAll
import it.twinsbrains.fpik.chapter8.Gen
import it.twinsbrains.fpik.chapter8.Gen.Companion.combine
import it.twinsbrains.fpik.chapter8.Prop
import it.twinsbrains.fpik.chapter9.Parsers.Parser

interface Parsers<PE> {
  interface Parser<A>

  fun <A> pure(a: A): Parser<A>

  infix fun <A> Parser<A>.or(a2: () -> Parser<A>): Parser<A>

  fun <A> listOfN(n: Int, p: Parser<A>): Parser<List<A>> =
    if (n < 0) {
      pure(emptyList())
    } else {
      map2(p, { listOfN(n - 1, p) }, { a, la -> listOf(a) + la })
    }

  fun <A> Parser<A>.many(): Parser<List<A>> = many1(this) or { pure(emptyList()) }

  fun <A, B> Parser<A>.map(f: (A) -> B): Parser<B> = this.flatMap { a -> pure(f(a)) }

  fun <A, B> Parser<A>.flatMap(f: (A) -> Parser<B>): Parser<B>

  fun <A> Parser<A>.slice(): Parser<String>

  infix fun <A, B> Parser<A>.product(pb: () -> Parser<B>): Parser<Pair<A, B>> = this.flatMap { a ->
    pb().map { b -> a to b }
  }

  fun <A, B, C> map2(
    pa: Parser<A>,
    pb: () -> Parser<B>,
    f: (A, B) -> C
  ): Parser<C> = (pa product pb).map { (a, b) -> f(a, b) }

  fun <A> many1(p: Parser<A>): Parser<List<A>> = map2(p, { p.many() }, { a, la -> listOf(a) + la })

  fun <A> run(p: Parser<A>, input: String): Either<PE, A>

  infix fun String.or(other: String): Parser<String> =
    pure(this) or { pure(other) }

  fun regexp(s: String): Parser<String>

  fun repeatedChar(aChar: Char): Parser<Int> =
    regexp("\\d{1}")
      .flatMap { c ->
        val size = c.toInt()
        listOfN(size, pure(aChar)).map { size }
      }
}

object ParseError

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