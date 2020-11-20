package it.twinsbrains.chapter2

import assertk.assertThat
import assertk.assertions.isEqualTo
import it.twinsbrains.chapter2.Currying.curry
import it.twinsbrains.chapter2.Currying.uncurry
import org.junit.Test

class CurryingTest {
    @Test
    fun currying() {
        val sum = { a: Int, b: Int -> a + b }
        assertThat(curry(sum)(2)(3)).isEqualTo(5)
    }

    @Test
    fun unCurrying() {
        val sum = { a: Int -> { b: Int -> a + b } }
        assertThat(uncurry(sum)(2, 3)).isEqualTo(5)
    }
}