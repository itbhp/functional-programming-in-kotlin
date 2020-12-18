package it.twinsbrains.fpik.chapter8

import it.twinsbrains.fpik.chapter7.Pars.equal
import it.twinsbrains.fpik.chapter7.Pars.map
import it.twinsbrains.fpik.chapter7.Pars.unit
import it.twinsbrains.fpik.chapter8.Prop.Companion.check
import it.twinsbrains.fpik.chapter8.Prop.Companion.run
import org.junit.jupiter.api.Test
import strikt.api.expectThat
import strikt.assertions.isA
import java.util.concurrent.Executors

class ParLawsTest {

  @Test
  fun identity() {
    val es = Executors.newCachedThreadPool()
    val p = check {
      val p1 = map(unit(1)) { it + 1 }
      val p2 = unit(2)
      equal(p1, p2)(es).get()
    }

    expectThat(run(p)).isA<Proved>()
  }
}