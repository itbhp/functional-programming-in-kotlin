package it.twinsbrains.fpik.chapter8

import arrow.mtl.run
import io.kotest.core.spec.style.StringSpec
import io.kotest.property.forAll
import it.twinsbrains.fpik.chapter6.LinearCongruentialGenerator
import kotlin.math.abs

class GenChooseSpecTest : StringSpec() {
  init {
    "all Gen.choose should be greater or equal than start and less that stopExclusive"{
      forAll<Pair<Int, Int>> {
        val sorted = listOf(abs(it.first), abs(it.second)).sorted()
        val start = sorted[0]
        val stopExclusive = sorted[1]
        val choose = Gen.choose(start, stopExclusive)
        val example = choose.sample.run(LinearCongruentialGenerator(3)).b
        example in start until stopExclusive
      }
    }
  }
}