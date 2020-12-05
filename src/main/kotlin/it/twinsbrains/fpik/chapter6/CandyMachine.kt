package it.twinsbrains.fpik.chapter6

import arrow.core.Tuple2
import arrow.mtl.State

object CandyMachine {
    sealed class Input {
        object Coin : Input()
        object Turn : Input()
    }

    data class Machine(
        val locked: Boolean,
        val candies: Int,
        val coins: Int
    )

    fun simulateMachine(
        inputs: List<Input>
    ): State<Machine, Tuple2<Int, Int>> = State { machine ->
        val lastMachine = inputs.fold(machine) { curMachine, i ->
            val (newMachine, _) = onAnInput(curMachine, i)
            newMachine
        }
        transitionTo(lastMachine)
    }

    private fun onAnInput(
        machine: Machine,
        input: Input
    ): Tuple2<Machine, Tuple2<Int, Int>> {
        val doNothing = transitionTo(machine)
        return when {
            input is Input.Turn && !machine.locked && machine.candies > 0 ->
                transitionTo(Machine(true, machine.candies - 1, machine.coins))
            input is Input.Coin && machine.locked && machine.candies > 0 ->
                transitionTo(Machine(false, machine.candies, machine.coins + 1))
            else -> doNothing
        }
    }

    private fun transitionTo(machine: Machine) =
        Tuple2(machine, Tuple2(machine.candies, machine.coins))
}
