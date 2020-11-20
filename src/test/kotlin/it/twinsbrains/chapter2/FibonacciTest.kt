package it.twinsbrains.chapter2

import org.junit.Test
import assertk.assertThat
import assertk.assertions.*
import it.twinsbrains.chapter2.Fibonacci.fibonacci

class FibonacciTest{

    @Test
    fun first() {
        assertThat(fibonacci(1)).isEqualTo(1)
    }

    @Test
    fun second() {
        assertThat(fibonacci(2)).isEqualTo(1)
    }

    @Test
    fun third() {
        assertThat(fibonacci(3)).isEqualTo(2)
    }

    @Test
    fun sixth() {
        assertThat(fibonacci(6)).isEqualTo(8)
    }
}