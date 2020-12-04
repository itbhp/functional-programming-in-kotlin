package it.twinsbrains.fpik.chapter6


data class State<S, out A>(val run: (S) -> Pair<A, S>) {

    companion object {
        fun <S, A> unit(a: A): State<S, A> = State { s -> a to s }

        fun <S, A, B> flatMap(f: State<S, A>, g: (A) -> State<S, B>): State<S, B> =
            State { s ->
                val (a, s1) = f.run(s)
                g(a).run(s1)
            }

        fun <S, A, B> map(s: State<S, A>, f: (A) -> B): State<S, B> =
            flatMap(s) { a -> State { s -> f(a) to s } }

        fun <A, B, C> map2(
            ra: Rand<A>,
            rb: Rand<B>,
            f: (A, B) -> C
        ): Rand<C> = flatMap(ra) { a -> map(rb) { b -> f(a, b) } }

        fun <A> sequence(fs: List<Rand<A>>): Rand<List<A>> =
            fs.fold(unit(listOf())) { acc, r -> map2(acc, r) { l, a -> l + a } }
    }

}