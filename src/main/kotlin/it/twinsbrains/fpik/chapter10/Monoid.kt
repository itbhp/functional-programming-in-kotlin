package it.twinsbrains.fpik.chapter10

import java.util.Optional

interface Monoid<A> {
  fun combine(a1: A, a2: A): A
  val nil: A
}

fun <A : Any> optionMonoid(): Monoid<Optional<A>> = object : Monoid<Optional<A>> {
  override fun combine(a1: Optional<A>, a2: Optional<A>): Optional<A> {
    return if (a1.isEmpty) a2 else a1
  }

  override val nil: Optional<A>
    get() = Optional.empty()

}

fun <A> dual(m: Monoid<A>): Monoid<A> = object : Monoid<A> {
  override fun combine(a1: A, a2: A): A {
    return m.combine(a2, a1)
  }

  override val nil: A
    get() = m.nil

}

