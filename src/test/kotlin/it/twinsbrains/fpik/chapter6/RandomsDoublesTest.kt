package it.twinsbrains.fpik.chapter6

import io.kotest.core.spec.style.StringSpec
import io.kotest.property.forAll
import it.twinsbrains.fpik.chapter6.Randoms.double
import it.twinsbrains.fpik.chapter6.LinearCongruentialGenerator as SimpleRNG

class RandomsDoublesTest : StringSpec() {
    init {
        "doubles should be in [0,1)"{
            forAll<Long> { seed ->
                val (num, _) = double(SimpleRNG(seed))
                num >= 0 && num < 1
            }
        }
    }
}