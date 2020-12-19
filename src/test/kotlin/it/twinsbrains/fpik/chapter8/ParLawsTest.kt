package it.twinsbrains.fpik.chapter8

import it.twinsbrains.fpik.chapter7.Par
import it.twinsbrains.fpik.chapter7.Pars
import it.twinsbrains.fpik.chapter7.Pars.equalTo
import it.twinsbrains.fpik.chapter7.Pars.map
import it.twinsbrains.fpik.chapter7.Pars.unit
import it.twinsbrains.fpik.chapter8.Prop.Companion.check
import it.twinsbrains.fpik.chapter8.Prop.Companion.run
import org.junit.jupiter.api.Test
import strikt.api.expectThat
import strikt.assertions.isA
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

class ParLawsTest {

  @Test
  fun mappingLaw() {
    val es = Executors.newCachedThreadPool()
    val p = check {
      val p1 = map(unit(1)) { it + 1 }
      val p2 = unit(2)
      es unwrap (p1 equalTo p2)
    }
    expectThat(run(p)).isA<Proved>()
  }

  private infix fun <A> ExecutorService.unwrap(p: Par<A>): A =
    Pars.run(this, p).get(100, TimeUnit.MILLISECONDS)
}