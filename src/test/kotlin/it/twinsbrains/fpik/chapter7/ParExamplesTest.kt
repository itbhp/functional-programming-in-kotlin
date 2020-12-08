package it.twinsbrains.fpik.chapter7

import it.twinsbrains.fpik.chapter7.ParExamples.sum
import it.twinsbrains.fpik.chapter7.Pars.fork
import it.twinsbrains.fpik.chapter7.Pars.lazyUnit
import it.twinsbrains.fpik.chapter7.Pars.unit
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import strikt.api.expectThat
import strikt.assertions.isEqualTo
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors.*
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException

class ParExamplesTest {
  @Test
  fun `sum on empty list`() {
    val res = Pars.run(newSingleThreadExecutor(), sum(listOf()))
    expectThat(res.get(100, TimeUnit.MILLISECONDS)).isEqualTo(0)
  }

  @Test
  fun `sum should wok`() {
    val res = Pars.run(newCachedThreadPool(), sum(listOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)))
    expectThat(res.get(100, TimeUnit.MILLISECONDS)).isEqualTo(55)
  }

  @Test
  fun `asyncF should work`() {
    val res = Pars.run(newCachedThreadPool(), Pars.asyncF<Int, String> { it.toString() }(1))
    expectThat(res.get(100, TimeUnit.MILLISECONDS)).isEqualTo("1")
  }

  @Test
  fun `sortPar should work`() {
    val res = Pars.run(newCachedThreadPool(), Pars.sortPar(unit(listOf(4, 5, 2, 3, 1))))
    expectThat(res.get(100, TimeUnit.MILLISECONDS)).isEqualTo(listOf(1, 2, 3, 4, 5))
  }

  @Test
  fun `parMap should work`() {
    val res = Pars.run(newCachedThreadPool(), Pars.parMap(listOf(4, 5, 2, 3, 1)) { it.toString() })
    expectThat(res.get(500, TimeUnit.MILLISECONDS)).isEqualTo(listOf("4", "5", "2", "3", "1"))
  }

  @Test
  fun `parFilter should work`() {
    val res = Pars.run(newCachedThreadPool(), Pars.parFilter(listOf(4, 5, 2, 3, 1)) { it < 4 })
    expectThat(res.get(500, TimeUnit.MILLISECONDS)).isEqualTo(listOf(2, 3, 1))
  }

  @Test
  fun `map3 should work`() {
    val res = Pars.map3(unit(1), unit(2), unit(4)) { a, b, c -> a + b + c }
    res.shouldBe(unit(7))(newCachedThreadPool())
  }

  @Test
  fun `deadlock due to fork impl`() {
    val a = lazyUnit { 43 }
    val b = fork { a }
    assertThrows<TimeoutException> {
      (a shouldBe b)(newFixedThreadPool(1))
      // during shouldBe on the right side we submit a callable inside another callable submit
      //having 1 thread only we miss another thread to spawn on the second callable and hence we have deadlock
    }
  }

  @Test
  fun `choiceN should work`() {
    val res = Pars.choiceN(unit(1), listOf(unit(2), unit(4)))
    res.shouldBe(unit(4))(newCachedThreadPool())
  }

  @Test
  fun `choice should work`() {
    val res = Pars.choice(unit(true), unit(2), unit(4))
    res.shouldBe(unit(2))(newCachedThreadPool())
  }

  @Test
  fun `choiceMap should work`() {
    val res = Pars.choiceMap(unit("a"), mapOf("a" to unit(2), "b" to unit(4)))
    res.shouldBe(unit(2))(newCachedThreadPool())
  }

  @Test
  fun `chooser should work`() {
    val res = Pars.chooser(unit("a"), { vA -> if (vA == "a") unit(2) else unit(4) })
    res.shouldBe(unit(2))(newCachedThreadPool())
  }

  private infix fun <A> Par<A>.shouldBe(other: Par<A>) = { es: ExecutorService ->
    if (this(es).get(500, TimeUnit.MILLISECONDS) != other(es).get(500, TimeUnit.MILLISECONDS))
      throw AssertionError("Par instances not equal")
  }
}



