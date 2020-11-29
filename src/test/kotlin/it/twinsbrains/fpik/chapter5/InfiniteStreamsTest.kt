package it.twinsbrains.fpik.chapter5

import it.twinsbrains.fpik.chapter3.List
import it.twinsbrains.fpik.chapter4.Option.Companion.none
import it.twinsbrains.fpik.chapter4.Option.Companion.some
import it.twinsbrains.fpik.chapter5.InfiniteStreams.fibs
import it.twinsbrains.fpik.chapter5.InfiniteStreams.ones
import it.twinsbrains.fpik.chapter5.InfiniteStreams.zipAll
import it.twinsbrains.fpik.chapter5.InfiniteStreams.zipWith
import it.twinsbrains.fpik.chapter5.Stream.Companion.exists
import it.twinsbrains.fpik.chapter5.Stream.Companion.of
import it.twinsbrains.fpik.chapter5.Stream.Companion.take
import it.twinsbrains.fpik.chapter5.Stream.Companion.toList
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

  @Test
  fun `from n infinite streams`() {
    expectThat(InfiniteStreams.from(5).take(5).toList())
      .isEqualTo(List.of(5, 6, 7, 8, 9))
  }

  @Test
  fun `fibonacci stream`() {
    expectThat(fibs().take(6).toList())
      .isEqualTo(List.of(0, 1, 1, 2, 3, 5))
  }

  @Test
  fun `zipWith should work on stream same length`() {
    expectThat(fibs().zipWith(fibs(), Int::plus).take(6).toList())
      .isEqualTo(List.of(0, 2, 2, 4, 6, 10))
  }

  @Test
  fun `zipWith should work on stream different length`() {
    expectThat(of(1, 2, 3).zipWith(of(1, 2), Int::plus).take(6).toList())
      .isEqualTo(List.of(2, 4))
  }

  @Test
  fun `zipAll on same length`() {
    expectThat(of(1, 2, 3).zipAll(of(1, 4, 9)).toList())
      .isEqualTo(List.of(some(1) to some(1), some(2) to some(4), some(3) to some(9)))
  }

  @Test
  fun `zipAll on different length`() {
    expectThat(of(1, 2, 3).zipAll(of(1, 4)).toList())
      .isEqualTo(List.of(some(1) to some(1), some(2) to some(4), some(3) to none()))
  }
}