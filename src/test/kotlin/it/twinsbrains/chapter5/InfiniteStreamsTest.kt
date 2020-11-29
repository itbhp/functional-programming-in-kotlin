package it.twinsbrains.chapter5

import it.twinsbrains.chapter3.List
import it.twinsbrains.chapter5.InfiniteStreams.ones
import it.twinsbrains.chapter5.Stream.Companion.exists
import it.twinsbrains.chapter5.Stream.Companion.take
import it.twinsbrains.chapter5.Stream.Companion.toList
import org.junit.Test
import strikt.api.expectThat
import strikt.assertions.isEqualTo
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

  @Test
  fun `constant infinite streams`() {
    expectThat(InfiniteStreams.constant(5).take(5).toList())
      .isEqualTo(List.of(5, 5, 5, 5, 5))
  }
}