package it.twinsbrains.fpik.chapter2

object Currying {
  fun <A, B, C> curry(f: (A, B) -> C): (A) -> (B) -> C = { a: A -> { b: B -> f(a, b) } }
  fun <A, B, C, D> curry(f: (A, B, C) -> D): (A) -> (B) -> (C) -> D =
    { a: A ->
      { b: B ->
        { c ->
          f(a, b, c)
        }
      }
    }

  fun <A, B, C> uncurry(f: (A) -> (B) -> C): (A, B) -> C = { a: A, b: B -> f(a)(b) }
}