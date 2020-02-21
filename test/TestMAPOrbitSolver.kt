import lib.KDTree
import lib.Multiset
import lib.divergence
import org.junit.Test

class TestMAPOrbitSolver {
    @Test
    fun fullTrajectoryTest() {
        System.loadLibrary("jniortools")

        // generate observation path
        val params = StandardParams
        val myModel = PredPreyModel(params)
        val startState = PredPreyModel.randomState(50, params)
        val observations = myModel.generateObservations(startState, 5, 0.5)
        println("Real orbit")
        observations.forEach {println(it.realState)}
        println("Observations")
        observations.forEach {println(it.observation)}

        val mySolver = MAPOrbitSolver(myModel, startState)
        observations.drop(1).forEach { mySolver.addObservation(it.observation) }
        mySolver.completeSolve()

        println("MAP orbit is")
        mySolver.timesteps.forEach { println(it.committedEvents) }

        println("history is")
        println(startState)
        mySolver.timesteps.forEach { println(it.committedState) }

        // compare states
        observations.map { it.realState }.zip(mySolver.timesteps.map { it.committedState }).forEach { (real, predicted) ->
            println("distance = ${predicted.divergence(real, params.GRIDSIZE)}  :  ${PredPreyModel.randomState(50,params).divergence(real, params.GRIDSIZE)}")
        }
    }

    @Test
    fun onlineTrajectoryTest() {
        System.loadLibrary("jniortools")

        val WINDOW_LEN = 5
        val TOTAL_STEPS = 15
        val params = StandardParams
        val myModel = PredPreyModel(params)
        val startState = PredPreyModel.randomState(50,params)
        val observations = myModel.generateObservations(startState, TOTAL_STEPS, 0.5)
        val windows = observations
            .drop(1)
            .windowed(WINDOW_LEN, WINDOW_LEN, true) {window ->
                window.map { obs -> obs.observation }
            }
        println("Real orbit")
        observations.forEach {println(it.realState)}
        println("Observations")
        observations.forEach {println(it.observation)}



        val mySolver = MAPOrbitSolver(myModel, startState)

        windows.forEach {window ->
            println("Adding window $window")
            mySolver.addObservations(window)
            mySolver.minimalSolve()

            println("MAP orbit is")
            mySolver.timesteps.forEach { println(it.committedEvents) }
            println("history is")
            println(startState)
            mySolver.timesteps.forEach { println(it.committedState) }
            println("sources are")
            println(mySolver.startState.sources)
            mySolver.timesteps.forEach { println(it.sources) }

            removeDeadAgents( mySolver, myModel,8)
        }
    }

    fun removeDeadAgents(solver: MAPOrbitSolver<Agent>, model: PredPreyModel, maxStepsUnseen: Int) {
        solver.timesteps.descendingIterator().asSequence().drop(maxStepsUnseen-1).forEach { timestep ->
            timestep.previousState.sources.forEach { agent ->
                model.deathEvents[agent]?.also {
                    timestep.committedEvents.add(it)
                }
            }
            timestep.previousState.sources.clear()
        }
    }


    fun stateNorm(from: Multiset<Agent>, to: Multiset<Agent>, gridSize: Int): Double {
        val kdTree = KDTree.Manhattan<Boolean>(2)
        to.supportSet.forEach { agent ->
            kdTree[agent.pos.rem(gridSize).toDouble(), agent.pos.div(gridSize).toDouble()] = true
        }
        return from.sumByDouble { agent ->
            kdTree.nearestNeighbour(agent.pos.rem(gridSize).toDouble(), agent.pos.div(gridSize).toDouble()).distance
        }/from.size
    }

}