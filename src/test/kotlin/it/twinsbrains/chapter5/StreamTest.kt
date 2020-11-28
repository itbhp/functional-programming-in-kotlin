package it.twinsbrains.chapter5

import assertk.assertThat
import assertk.assertions.isEqualTo
import it.twinsbrains.chapter4.Option.Companion.some
import it.twinsbrains.chapter5.Stream.Companion.headOption
import org.junit.Test

class StreamTest {
  @Test
  fun `headOption should work`() {
    assertThat(Cons({ 1 }, { Cons({ 2 }, { Empty }) }).headOption()).isEqualTo(some(1))
  }
}