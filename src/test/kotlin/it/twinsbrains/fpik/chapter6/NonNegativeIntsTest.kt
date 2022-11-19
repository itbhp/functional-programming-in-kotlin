package it.twinsbrains.fpik.chapter6

import io.kotest.core.spec.style.WordSpec
import io.kotest.property.forAll
import it.twinsbrains.fpik.chapter6.Randoms.nonNegativeInt
import it.twinsbrains.fpik.chapter6.LinearCongruentialGenerator as SimpleRNG

class NonNegativeIntsTest : WordSpec({

  "NonNegativeInt" should {
    "have no elements less than 0" {
      forAll<Long> { seed ->
        nonNegativeInt(SimpleRNG(seed)).first >= 0
      }
    }

    "have no elements greater than Integer.MAX_VALUE" {
      forAll<Long> { seed ->
        nonNegativeInt(SimpleRNG(seed)).first < Integer.MAX_VALUE
      }
    }
  }
})