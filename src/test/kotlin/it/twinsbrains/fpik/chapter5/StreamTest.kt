package it.twinsbrains.fpik.chapter5

import it.twinsbrains.fpik.StriktExtensionForOurFunctionalLibrary.isNone
import it.twinsbrains.fpik.chapter3.List
import it.twinsbrains.fpik.chapter4.Option.Companion.some
import it.twinsbrains.fpik.chapter5.Stream.Companion.append
import it.twinsbrains.fpik.chapter5.Stream.Companion.cons
import it.twinsbrains.fpik.chapter5.Stream.Companion.drop
import it.twinsbrains.fpik.chapter5.Stream.Companion.empty
import it.twinsbrains.fpik.chapter5.Stream.Companion.exists
import it.twinsbrains.fpik.chapter5.Stream.Companion.filter
import it.twinsbrains.fpik.chapter5.Stream.Companion.find
import it.twinsbrains.fpik.chapter5.Stream.Companion.flatMap
import it.twinsbrains.fpik.chapter5.Stream.Companion.forAll
import it.twinsbrains.fpik.chapter5.Stream.Companion.hasSubsequence
import it.twinsbrains.fpik.chapter5.Stream.Companion.headOption
import it.twinsbrains.fpik.chapter5.Stream.Companion.map
import it.twinsbrains.fpik.chapter5.Stream.Companion.startsWith
import it.twinsbrains.fpik.chapter5.Stream.Companion.tails
import it.twinsbrains.fpik.chapter5.Stream.Companion.take
import it.twinsbrains.fpik.chapter5.Stream.Companion.takeWhile
import it.twinsbrains.fpik.chapter5.Stream.Companion.toList
import org.junit.After
import org.junit.Before
import org.junit.Test
import strikt.api.expect
import strikt.api.expectThat
import strikt.assertions.isEqualTo
import strikt.assertions.isFalse
import strikt.assertions.isTrue
import java.io.ByteArrayOutputStream
import java.io.PrintStream

class StreamTest {

  private lateinit var previousOut: PrintStream

  private val testOutput = ByteArrayOutputStream()

  private val capturedOut: kotlin.collections.List<String>
    get() {
      return testOutput.toString().split("\n")
    }

  @Before
  fun setUp() {
    previousOut = System.out
    val ps = PrintStream(testOutput)
    System.setOut(ps)
  }

  @After
  fun tearDown() {
    System.setOut(previousOut)
  }

  @Test
  fun `headOption should work`() {
    expectThat(Cons({ 1 }, { Cons({ 2 }, { Empty }) }).headOption()).isEqualTo(some(1))
  }

  @Test
  fun `proof of memoization of smart constructors`() {
    val anInt: () -> Int = {
      print("an Int being created")
      4
    }

    val aStream = cons(anInt, { cons({ 2 }, { empty() }) })
    aStream.headOption()
    aStream.headOption()

    expectThat(capturedOut.size).isEqualTo(1)
    expectThat(capturedOut[0]).isEqualTo("an Int being created")
  }

  @Test
  fun `stream of should work`() {
    val stream = Stream.of(1, 2, 3)

    expectThat(stream.headOption()).isEqualTo(some(1))
  }

  @Test
  fun `toList should work`() {
    val stream = Stream.of(1, 2, 3)

    expectThat(stream.toList()).isEqualTo(List.of(1, 2, 3))
  }

  @Test
  fun `take test`() {
    expectThat(Stream.of(1, 2, 3, 4, 5, 6, 7).take(3).toList()).isEqualTo(List.of(1, 2, 3))
  }

  @Test
  fun `drop test`() {
    expectThat(Stream.of(1, 2, 3, 4, 5, 6, 7).drop(3).toList()).isEqualTo(List.of(4, 5, 6, 7))
  }

  @Test
  fun `takeWhile n`() {
    expectThat(Stream.of(1, 2, 3, 4, 5, 6, 7).takeWhile { it < 4 }.toList())
      .isEqualTo(List.of(1, 2, 3))
  }

  @Test
  fun `exists should work`() {
    expectThat(Stream.of(2, 3, 4, 5).exists { it % 2 == 0 }).isTrue()
  }

  @Test
  fun `forAll test`() {
    expectThat(Stream.of(2, 3, 4, 5).forAll { it % 2 == 0 }).isFalse()
    expectThat(Stream.of(2, 3, 4, 5).forAll { it < 6 }).isTrue()
  }

  @Test
  fun `map should work`() {
    expectThat(Stream.of(1, 2, 4).map { it.toString() }.toList())
      .isEqualTo(List.of("1", "2", "4"))
  }

  @Test
  fun `filter should work`() {
    expectThat(Stream.of(1, 2, 3, 4).filter { it % 2 == 0 }.toList())
      .isEqualTo(List.of(2, 4))
  }

  @Test
  fun `append should work`() {
    expectThat(Stream.of(1, 2, 3, 4).append(Stream.of(5, 6)).toList())
      .isEqualTo(List.of(1, 2, 3, 4, 5, 6))
  }

  @Test
  fun `flatMap should work`() {
    expectThat(Stream.of(1, 2, 4).flatMap { Stream.of(it.toString()) }.toList())
      .isEqualTo(List.of("1", "2", "4"))
  }

  @Test
  fun `find test`() {
    expectThat(Stream.of(1, 2, 4).find { it % 2 == 0 })
      .isEqualTo(some(2))

    expectThat(Stream.of(1, 2, 4).find { it > 5 })
      .isNone()
  }

  @Test
  fun `startsWith should work`() {
    expectThat(Stream.of(1, 2, 4).startsWith(Stream.of(1, 2)))
      .describedAs("stream (1, 2, 4) starts with stream (1, 2)")
      .isTrue()

    expectThat(Stream.of(1, 2).startsWith(Stream.of(1, 2, 3))).isFalse()
  }

  @Test
  fun `tails should work`() {
    expect {
      that(Stream.of(1, 2, 3).tails()) {
        get("as list") { map { e -> e.toList() }.toList() }
          .isEqualTo(List.of(List.of(1, 2, 3), List.of(2, 3), List.of(3)))
      }
    }
  }

  @Test
  fun `hasSubsequence should work`() {
    expectThat(Stream.of(1, 2, 3).hasSubsequence(Stream.of(2, 3))).isTrue()
    expectThat(Stream.of(1, 2, 3).hasSubsequence(Stream.of(3, 4))).isFalse()
  }
}