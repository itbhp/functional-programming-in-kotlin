package it.twinsbrains.fpik.chapter8

import it.twinsbrains.fpik.chapter7.Par
import it.twinsbrains.fpik.chapter7.Pars.equalTo
import it.twinsbrains.fpik.chapter7.Pars.fork
import it.twinsbrains.fpik.chapter7.Pars.map
import it.twinsbrains.fpik.chapter7.Pars.unit
import it.twinsbrains.fpik.chapter8.Checkers.forAllPar
import it.twinsbrains.fpik.chapter8.Prop.Companion.run
import org.junit.jupiter.api.Test
import strikt.api.expectThat
import strikt.assertions.isA

class ParLawsTest {
  private val pint: Gen<Par<Int>> =
    Gen.choose(0, 10).map {
      unit(it)
    }

  @Test
  fun mappingLaw() {
    val p = forAllPar(pint) { n: Par<Int> ->
      map(n) { it } equalTo n
    }
    expectThat(run(p)).isA<Passed>()
  }

  @Test
  fun forkLaw() {
    val p = forAllPar(pint) { n: Par<Int> ->
      fork { n } equalTo n
    }
    expectThat(run(p)).isA<Passed>()
  }
}