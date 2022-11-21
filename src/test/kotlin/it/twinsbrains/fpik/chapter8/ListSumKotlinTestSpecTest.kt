package it.twinsbrains.fpik.chapter8

import io.kotest.core.spec.style.StringSpec
import io.kotest.property.Arb
import io.kotest.property.arbitrary.constant
import io.kotest.property.arbitrary.int
import io.kotest.property.arbitrary.list
import io.kotest.property.forAll

class ListSumKotlinTestSpecTest : StringSpec() {
  init {
    "sum of a list and reversed list should be the same" {
      val intList = Arb.list(Arb.int(1..100))
      forAll(intList) { l ->
        l.sum() == l.reversed().sum()
      }
    }

    "sum of a list and sorted list should be the same" {
      val intList = Arb.list(Arb.int(1..100))
      forAll(intList) { l ->
        l.sum() == l.sorted().sum()
      }
    }

    "sum of a list containing the same element should be size * element" {
      val intList = Arb.list(Arb.constant(4))
      forAll(intList) { l ->
        l.sum() == 4 * l.size
      }
    }

    "sum of a list containing number from 1 to n should be n * (n+1)/2" {
      val size = Arb.int(1..10)
      forAll(size) { s ->
        val list = (1..s).toList()
        list.sum() == s * (s + 1) / 2
      }
    }
  }
}