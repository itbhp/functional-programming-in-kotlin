package it.twinsbrains

import it.twinsbrains.chapter4.None
import it.twinsbrains.chapter4.Option
import strikt.api.Assertion


object StriktExtensionForOurFunctionalLibrary {
  fun <T> Assertion.Builder<Option<T>>.isNone(): Assertion.Builder<Option<T>> =
    assert("should be None") {
      when (it) {
        is None -> pass()
        else -> fail()
      }
    }
}