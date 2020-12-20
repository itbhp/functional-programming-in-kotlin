package it.twinsbrains.fpik.chapter8

import it.twinsbrains.fpik.chapter7.Par
import it.twinsbrains.fpik.chapter7.Pars.equalTo
import it.twinsbrains.fpik.chapter7.Pars.map
import it.twinsbrains.fpik.chapter7.Pars.unit
import it.twinsbrains.fpik.chapter8.Checkers.checkPar
import it.twinsbrains.fpik.chapter8.Prop.Companion.run
import org.junit.jupiter.api.Test
import strikt.api.expectThat
import strikt.assertions.isA

class ParLawsTest {

  @Test
  fun mappingLaw() {
    val p = checkPar(run {
      val p1: Par<Int> = map(unit(1)) { it + 1 }
      val p2: Par<Int> = unit(2)
      p1 equalTo p2
    })
    expectThat(run(p)).isA<Passed>()
  }
}