package it.twinsbrains.fpik.chapter7

import it.twinsbrains.fpik.chapter7.ParExamples.sum
import it.twinsbrains.fpik.chapter7.Pars.unit
import org.junit.jupiter.api.Test
import strikt.api.expectThat
import strikt.assertions.isEqualTo
import java.util.concurrent.Executors.newCachedThreadPool
import java.util.concurrent.Executors.newSingleThreadExecutor
import java.util.concurrent.TimeUnit

class ParExamplesTest {
  @Test
  fun `sum on empty list`() {
    val res = Pars.run(newSingleThreadExecutor(), sum(listOf()))
    expectThat(res.get(5, TimeUnit.MILLISECONDS)).isEqualTo(0)
  }

  @Test
  fun `sum should wok`() {
    val res = Pars.run(newCachedThreadPool(), sum(listOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)))
    expectThat(res.get(5, TimeUnit.MILLISECONDS)).isEqualTo(55)
  }

  @Test
  fun `asyncF should work`() {
    val res = Pars.run(newCachedThreadPool(), Pars.asyncF<Int, String> { it.toString() }(1))
    expectThat(res.get(5, TimeUnit.MILLISECONDS)).isEqualTo("1")
  }

  @Test
  fun `sortPar should work`() {
    val res = Pars.run(newCachedThreadPool(), Pars.sortPar(unit(listOf(4, 5, 2, 3, 1))))
    expectThat(res.get(5, TimeUnit.MILLISECONDS)).isEqualTo(listOf(1, 2, 3, 4, 5))
  }

  @Test
  fun `parMap should work`() {
    val res = Pars.run(newCachedThreadPool(), Pars.parMap(listOf(4, 5, 2, 3, 1)) { it.toString() })
    expectThat(res.get(500, TimeUnit.MILLISECONDS)).isEqualTo(listOf("4", "5", "2", "3", "1"))
  }
}
