package it.twinsbrains.fpik.chapter7

import it.twinsbrains.fpik.chapter7.ParExamples.sum
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
}
