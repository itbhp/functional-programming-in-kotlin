package it.twinsbrains.fpik.chapter8

import arrow.mtl.run
import io.kotest.core.spec.style.StringSpec
import io.kotest.property.checkAll
import io.kotest.property.withAssumptions
import it.twinsbrains.fpik.chapter6.LinearCongruentialGenerator

class GenChooseSpecTest : StringSpec() {
  init {
    "all Gen.choose should be greater or equal than start and less that stopExclusive" {
      checkAll<Pair<Int, Int>> { (a, b) ->
        withAssumptions(a != b && a > 0 && a > b) {
          val choose = Gen.choose(a, b)
          val example = choose.sample.run(LinearCongruentialGenerator(3)).b
          example in a until b
        }
      }
    }
  }
}