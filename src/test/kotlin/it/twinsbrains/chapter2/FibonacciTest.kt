package it.twinsbrains.chapter2

import org.junit.Test
import assertk.assertThat
import assertk.assertions.*
import it.twinsbrains.chapter2.Fibonacci.fibonacci

class FibonacciTest{
    @Test
    fun `for 3`() {
        assertThat(fibonacci(3)).isEqualTo(6)
    }

    @Test
    fun `for 6`() {
        assertThat(fibonacci(6)).isEqualTo(720)
    }
}