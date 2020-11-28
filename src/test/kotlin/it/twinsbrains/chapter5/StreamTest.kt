package it.twinsbrains.chapter5

import it.twinsbrains.chapter3.List
import it.twinsbrains.chapter4.Option.Companion.some
import it.twinsbrains.chapter5.Stream.Companion.cons
import it.twinsbrains.chapter5.Stream.Companion.drop
import it.twinsbrains.chapter5.Stream.Companion.empty
import it.twinsbrains.chapter5.Stream.Companion.headOption
import it.twinsbrains.chapter5.Stream.Companion.take
import it.twinsbrains.chapter5.Stream.Companion.takeWhile
import it.twinsbrains.chapter5.Stream.Companion.toList
import org.junit.After
import org.junit.Before
import org.junit.Test
import strikt.api.expectThat
import strikt.assertions.isEqualTo
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
    expectThat(Stream.of(1, 2, 3, 4, 5, 6, 7).takeWhile { it < 4 }.toList()).isEqualTo(List.of(1, 2, 3))
  }
}