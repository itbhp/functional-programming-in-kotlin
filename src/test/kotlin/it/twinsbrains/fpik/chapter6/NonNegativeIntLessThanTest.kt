package it.twinsbrains.fpik.chapter6

import io.kotest.core.spec.style.StringSpec
import io.kotest.property.forAll
import it.twinsbrains.fpik.chapter6.RandExamples.nonNegativeIntLessThan
import it.twinsbrains.fpik.chapter6.LinearCongruentialGenerator as SimpleRNG

class NonNegativeIntLessThanTest : StringSpec() {
    init {
        "all r should be >= 0 and <n"{
            forAll<Long> { seed ->
                val n = 100
                val (num, _) = nonNegativeIntLessThan(n).run(SimpleRNG(seed))
                num in 0 until n
            }
        }
    }
}