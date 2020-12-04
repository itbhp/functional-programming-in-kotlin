package it.twinsbrains.fpik.chapter2

import it.twinsbrains.fpik.chapter2.Fibonacci.fibonacci
import org.junit.jupiter.api.Test
import strikt.api.expectThat
import strikt.assertions.isEqualTo

class FibonacciTest{

    @Test
    fun first() {
      expectThat(fibonacci(1)).isEqualTo(1)
    }

    @Test
    fun second() {
      expectThat(fibonacci(2)).isEqualTo(1)
    }

    @Test
    fun third() {
      expectThat(fibonacci(3)).isEqualTo(2)
    }

    @Test
    fun sixth() {
      expectThat(fibonacci(6)).isEqualTo(8)
    }
}