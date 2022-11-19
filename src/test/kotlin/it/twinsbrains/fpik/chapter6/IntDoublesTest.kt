package it.twinsbrains.fpik.chapter6

import io.kotest.core.spec.style.StringSpec
import io.kotest.property.forAll
import it.twinsbrains.fpik.chapter6.RandExamples.intDoubleR
import it.twinsbrains.fpik.chapter6.LinearCongruentialGenerator as SimpleRNG

class IntDoublesTest : StringSpec() {
    init {
        "int from the pair should be positives " {
            forAll<Long> { seed ->
                val (p, _) = intDoubleR.myRun(SimpleRNG(seed))
                val (i, _) = p
                i >= 0
            }
        }

        "double from the pair should be in [0,1) " {
            forAll<Long> { seed ->
                val (p, _) = intDoubleR.myRun(SimpleRNG(seed))
                val (_, d) = p
                d >= 0 && d < 1
            }
        }
    }
}