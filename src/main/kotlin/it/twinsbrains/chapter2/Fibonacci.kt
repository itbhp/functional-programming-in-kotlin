package it.twinsbrains.chapter2

object Fibonacci {
    fun fibonacci(n: Int): Int {
        fun loop(i: Int, acc: Int): Int =
            if (i == 0) {
                acc
            } else {
                loop(i - 1, acc * i)
            }
        return loop(n, 1)
    }
}