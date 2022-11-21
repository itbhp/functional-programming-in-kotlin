package it.twinsbrains.fpik

import it.twinsbrains.fpik.chapter4.None
import it.twinsbrains.fpik.chapter4.Option
import strikt.api.Assertion

object AssertionsForOurFunctionalLibrary {
  fun <T> Assertion.Builder<Option<T>>.isNone(): Assertion.Builder<Option<T>> =
    assert("should be None") {
      when (it) {
        is None -> pass()
        else -> fail()
      }
    }
}