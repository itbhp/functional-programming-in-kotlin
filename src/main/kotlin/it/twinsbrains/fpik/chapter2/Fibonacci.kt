package it.twinsbrains.fpik.chapter2

object Fibonacci {
    fun fibonacci(n: Int): Int {
        tailrec fun loop(i: Int, prev: Int, last: Int): Int =
            if (i == 0) {
                prev
            } else {
                loop(i - 1, last, last + prev)
            }
        return loop(n, 0, 1)
    }
}