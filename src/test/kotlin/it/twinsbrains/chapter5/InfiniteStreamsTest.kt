package it.twinsbrains.chapter5

import it.twinsbrains.chapter5.InfiniteStreams.ones
import it.twinsbrains.chapter5.Stream.Companion.exists
import org.junit.Test
import strikt.api.expectThat
import strikt.assertions.isTrue

class InfiniteStreamsTest {

  @Test
  fun `exists terminates as soon as possible on infinite streams if verified`() {
    expectThat(ones().exists { it % 2 != 0 }).isTrue()
  }

  @Test(expected = StackOverflowError::class)
  fun `exists will stack overflow on infinite streams if not verified`() {
    ones().exists { it % 2 == 0 }
  }

}