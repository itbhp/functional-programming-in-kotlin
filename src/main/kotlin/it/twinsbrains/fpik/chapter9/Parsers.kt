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

  fun <A> or(a1: Parser<A>, a2: Parser<A>): Parser<A>

  fun <A> listOfN(n: Int, p: Parser<A>): Parser<List<A>>

  fun <A> Parser<A>.many(): Parser<List<A>>

  fun <A, B> Parser<A>.map(f: (A) -> B): Parser<B>

  fun <A> Parser<A>.slice(): Parser<String>

  infix fun <A, B> Parser<A>.product(pb: Parser<B>): Parser<Pair<A, B>>

  fun <A, B, C> map2(
    pa: Parser<A>,
    pb: () -> Parser<B>,
    f: (A, B) -> C
  ): Parser<C> = (pa product pb()).map { (a, b) -> f(a, b) }

  fun <A> many1(p: Parser<A>): Parser<List<A>> = map2(p, { p.many() }, { a, la -> listOf(a) + la })

  fun <A> run(p: Parser<A>, input: String): Either<PE, A>

  infix fun String.or(other: String): Parser<String> =
    or(pure(this), pure(other))
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

  fun orAssociativity(gc: Gen<String>): Prop = forAll(gc combine gc combine gc) { (ab, c) ->
    val (a, b) = ab
    val right = or(pure(a), (b or c))
    val left = or((a or b), pure(c))
    run(right, a) == run(left, a) &&
      run(right, b) == run(left, b) &&
      run(right, c) == run(left, c)
  }
}