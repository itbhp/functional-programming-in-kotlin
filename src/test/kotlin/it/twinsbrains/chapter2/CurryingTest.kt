package it.twinsbrains.chapter2

import it.twinsbrains.chapter2.Currying.curry
import it.twinsbrains.chapter2.Currying.uncurry
import org.junit.Test
import strikt.api.expectThat
import strikt.assertions.isEqualTo

class CurryingTest {
    @Test
    fun currying() {
        val sum = { a: Int, b: Int -> a + b }
      expectThat(curry(sum)(2)(3)).isEqualTo(5)
    }

    @Test
    fun unCurrying() {
        val sum = { a: Int -> { b: Int -> a + b } }
      expectThat(uncurry(sum)(2, 3)).isEqualTo(5)
    }
}