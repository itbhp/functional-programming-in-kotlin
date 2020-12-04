package it.twinsbrains.fpik.chapter6


data class AState<S, out A>(val run: (S) -> Pair<A, S>) {

    companion object {
        fun <S, A> unit(a: A): AState<S, A> = AState { s -> a to s }

        fun <S, A, B> flatMap(f: AState<S, A>, g: (A) -> AState<S, B>): AState<S, B> =
            AState { s ->
                val (a, s1) = f.run(s)
                g(a).run(s1)
            }

        fun <S, A, B> map(s: AState<S, A>, f: (A) -> B): AState<S, B> =
            flatMap(s) { a -> AState { s -> f(a) to s } }

        fun <A, B, C> map2(
            ra: Rand<A>,
            rb: Rand<B>,
            f: (A, B) -> C
        ): Rand<C> = flatMap(ra) { a -> map(rb) { b -> f(a, b) } }

        fun <A> sequence(fs: List<Rand<A>>): Rand<List<A>> =
            fs.fold(unit(listOf())) { acc, r -> map2(acc, r) { l, a -> l + a } }
    }

}