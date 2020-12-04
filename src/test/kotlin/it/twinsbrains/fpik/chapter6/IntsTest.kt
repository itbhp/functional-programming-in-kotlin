package it.twinsbrains.fpik.chapter6

import io.kotest.core.spec.style.StringSpec
import io.kotest.property.forAll
import it.twinsbrains.fpik.chapter3.List.Companion.length
import it.twinsbrains.fpik.chapter6.RandExamples.intsR
import it.twinsbrains.fpik.chapter6.Randoms.ints
import it.twinsbrains.fpik.chapter6.LinearCongruentialGenerator as SimpleRNG

class IntsTest : StringSpec() {
    init {
        "random ints" {
            forAll<Long> { seed ->
                val n = 100
                val (list, _) = ints(n, SimpleRNG(seed))
                length(list) == n
            }
        }

        "random intsR" {
            forAll<Long> { seed ->
                val n = 100
                val (list, _) = intsR(n)(SimpleRNG(seed))
                list.size == n
            }
        }
    }

}