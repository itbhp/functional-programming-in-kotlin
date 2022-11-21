package it.twinsbrains.fpik.chapter6

import arrow.mtl.run
import it.twinsbrains.fpik.chapter6.CandyMachine.Input
import it.twinsbrains.fpik.chapter6.CandyMachine.Input.Coin
import it.twinsbrains.fpik.chapter6.CandyMachine.Input.Turn
import it.twinsbrains.fpik.chapter6.CandyMachine.Machine
import it.twinsbrains.fpik.chapter6.CandyMachine.simulateMachine
import org.junit.jupiter.api.Test
import strikt.api.expectThat
import strikt.assertions.isEqualTo

class CandyMachineTest {
  @Test
  fun `inserting a coin on out of candy machine`() {
    val inputs = listOf<Input>(Coin)
    val simulateMachine = simulateMachine(inputs)
    val initialMachine = Machine(true, 0, 1)
    val (newMachine, _) = simulateMachine.run(initialMachine)

    expectThat(newMachine).isEqualTo(initialMachine)
  }

  @Test
  fun `trying to turn the knob on an out of candy machine`() {
    val inputs = listOf<Input>(Turn)
    val simulateMachine = simulateMachine(inputs)
    val initialMachine = Machine(false, 0, 1)
    val (newMachine, _) = simulateMachine.run(initialMachine)

    expectThat(newMachine).isEqualTo(initialMachine)
  }

  @Test
  fun `turning the knob on locked machine`() {
    val initialMachine = Machine(true, 1, 1)
    val inputs = listOf<Input>(Turn)
    val simulateMachine = simulateMachine(inputs)
    val (newMachine, _) = simulateMachine.run(initialMachine)

    expectThat(newMachine).isEqualTo(initialMachine)
  }

  @Test
  fun `inserting a coin on unlocked machine`() {
    val initialMachine = Machine(false, 1, 1)
    val inputs = listOf<Input>(Coin)
    val simulateMachine = simulateMachine(inputs)
    val (newMachine, _) = simulateMachine.run(initialMachine)

    expectThat(newMachine).isEqualTo(initialMachine)
  }

  @Test
  fun `turning the knob on unlocked machine with candies`() {
    val initialMachine = Machine(false, 1, 1)
    val inputs = listOf<Input>(Turn)
    val simulateMachine = simulateMachine(inputs)
    val (newMachine, _) = simulateMachine.run(initialMachine)

    expectThat(newMachine).isEqualTo(Machine(true, 0, 1))
  }

  @Test
  fun `inserting coin on locked machine with candies`() {
    val initialMachine = Machine(true, 1, 1)
    val inputs = listOf<Input>(Coin)
    val simulateMachine = simulateMachine(inputs)
    val (newMachine, _) = simulateMachine.run(initialMachine)

    expectThat(newMachine).isEqualTo(Machine(false, 1, 2))
  }

  @Test
  fun `inserting coin on locked machine with candies and then turn the knob`() {
    val initialMachine = Machine(true, 2, 1)
    val inputs = listOf(Coin, Turn)
    val simulateMachine = simulateMachine(inputs)
    val (newMachine, _) = simulateMachine.run(initialMachine)

    expectThat(newMachine).isEqualTo(Machine(true, 1, 2))
  }
}