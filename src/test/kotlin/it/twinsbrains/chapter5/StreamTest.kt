package it.twinsbrains.chapter5

import assertk.assertThat
import assertk.assertions.isEqualTo
import it.twinsbrains.chapter4.Option.Companion.some
import it.twinsbrains.chapter5.Stream.Companion.cons
import it.twinsbrains.chapter5.Stream.Companion.empty
import it.twinsbrains.chapter5.Stream.Companion.headOption
import org.junit.After
import org.junit.Before
import org.junit.Test
import java.io.ByteArrayOutputStream
import java.io.PrintStream

class StreamTest {

  private lateinit var previousOut: PrintStream

  private val testOutput = ByteArrayOutputStream()

  private val capturedOut: List<String>
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
    assertThat(Cons({ 1 }, { Cons({ 2 }, { Empty }) }).headOption()).isEqualTo(some(1))
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

    assertThat(capturedOut.size).isEqualTo(1)
    assertThat(capturedOut[0]).isEqualTo("an Int being created")
  }

  @Test
  fun `stream of should work`() {
    val stream = Stream.of(1, 2, 3)

    assertThat(stream.headOption()).isEqualTo(some(1))
  }
}