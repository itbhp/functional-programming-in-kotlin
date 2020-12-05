package it.twinsbrains.fpik.chapter6

import arrow.core.Id
import arrow.core.Tuple2
import arrow.core.extensions.id.monad.monad
import arrow.mtl.State
import arrow.mtl.StateApi
import arrow.mtl.extensions.fx
import arrow.mtl.stateSequential

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
        input: Input
    ): (Machine) -> Machine = { machine ->
        when {
            input is Input.Turn && !machine.locked && machine.candies > 0 ->
                Machine(true, machine.candies - 1, machine.coins)
            input is Input.Coin && machine.locked && machine.candies > 0 ->
                Machine(false, machine.candies, machine.coins + 1)
            else -> machine
        }
    }

    fun simulateMachine(
        inputs: List<Input>
    ): State<Machine, Unit> = State.fx(Id.monad()) {
        val aMap: List<(Machine) -> Machine> = inputs.map(::transform)
        val anotherMap: List<State<Machine, Unit>> = aMap.map(StateApi::modify)
        val x: List<Unit> = anotherMap.stateSequential().bind()
        val s: Machine = StateApi.get<Machine>().bind()
        Tuple2(s.candies, s.coins)
    }

}
