package it.twinsbrains.fpik.chapter6

import arrow.core.extensions.IdMonad
import arrow.mtl.State
import arrow.mtl.extensions.fx
import arrow.mtl.run
import it.twinsbrains.fpik.chapter6.StateMonadComprehension.int
import it.twinsbrains.fpik.chapter6.StateMonadComprehension.ints
import org.junit.jupiter.api.Test
import strikt.api.expectThat
import strikt.assertions.isEqualTo

class StateMonadComprehensionTest {
    @Test
    fun `state monad comprehension`() {
        val ns2: State<RNG, List<Int>> = State.fx(object : IdMonad {}) {
            val x: Int = int.bind()
            val y: Int = int.bind()
            val xs: List<Int> = ints(x).bind()
            xs.map { it % y }
        }

        val listInt = ns2.run(Fixed(2)).b

        expectThat(listInt.size).isEqualTo(2)
    }
}

class Fixed(private val n: Int) : RNG {
    override fun nextInt(): Pair<Int, RNG> {
        return n to this
    }
}