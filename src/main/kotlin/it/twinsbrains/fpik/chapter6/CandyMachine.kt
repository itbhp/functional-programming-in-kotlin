package it.twinsbrains.fpik.chapter6

import arrow.core.Tuple2
import arrow.mtl.State
import arrow.mtl.run
import it.twinsbrains.fpik.chapter6.ArrowStateMonad.modify

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

    private fun transform(
        machine: Machine,
        input: Input
    ): Machine {
        return when {
            input is Input.Turn && !machine.locked && machine.candies > 0 ->
                Machine(true, machine.candies - 1, machine.coins)
            input is Input.Coin && machine.locked && machine.candies > 0 ->
                Machine(false, machine.candies, machine.coins + 1)
            else -> machine
        }
    }

    fun simulateMachine(
        inputs: List<Input>
    ): State<Machine, Unit> = State { machine ->
        inputs.map { i ->
            modify<Machine> { s -> transform(s, i) }
        }.fold(Tuple2(machine, Unit)) { cur, s -> s.run(cur.a) }
    }
}
