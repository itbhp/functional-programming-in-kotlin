package it.twinsbrains.fpik.chapter6

import arrow.core.Tuple2
import arrow.core.extensions.IdMonad
import arrow.mtl.State
import arrow.mtl.stateSequential

object StateMonadComprehension {

    val int: State<RNG, Int> = State { rng ->
        val (n, nRng) = rng.nextInt()
        Tuple2(nRng, n)
    }

    private val nonNegativeInt: State<RNG, Int> = map(int) { if (it < 0) -(it + 1) else it }

    fun ints(count: Int): State<RNG, List<Int>> = sequence((1..count).map { nonNegativeInt })

    fun <A, B> flatMap(
        s: State<RNG, A>,
        f: (A) -> State<RNG, B>
    ): State<RNG, B> = s.flatMap(object : IdMonad {}, f)

    fun <A, B> map(
        s: State<RNG, A>, f: (A) -> B
    ): State<RNG, B> = flatMap(s) { a -> State { rng -> Tuple2(rng, f(a)) } }

    private fun <A> sequence(fs: List<State<RNG, A>>): State<RNG, List<A>> =
        fs.stateSequential()

}