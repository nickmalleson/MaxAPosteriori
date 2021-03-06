import java.io.Serializable

open class Params(val GRIDSIZE: Int,
             val lambdaPred: Double,
             val lambdaPrey: Double,
             val preyDie: Double,
             val preyReproduce: Double,
             val preyDiffuse: Double,
             val predDie: Double,
             val predCaptureOnly: Double,
             val predCaptureAndReproduce: Double,
             val predDiffuse: Double): Serializable {
    val GRIDSIZESQ = GRIDSIZE*GRIDSIZE
    val predCapture = predCaptureAndReproduce + predCaptureOnly
    val preyTotal = preyDie + preyDiffuse + preyReproduce
}

object StandardParams : Params(
        32,
        0.04,
        0.04,

        0.03,           // these three should add up to 1.0
        0.06,
        0.91,

        0.05,
        0.0,       // captures must add up to preyDiffuse
        0.91,
        0.95            // predDiffuse + predDie should add to 1.0
)

object TenByTenParams : Params(
        10,
        0.12,
        0.12,

        0.03,
        0.06,
        0.91,

        0.07,
        0.091,//0.05,
        0.819,
        0.93
)


//val MediumParams = Params(
//        8,
//        0.04,
//        0.08,
//        0.03,
//        0.06,
//        1.0,
//        0.07,
//        0.05,
//        0.5,
//        1.0
//)
//
//
//object SmallParams : Params(
//        3,
//        0.25,
//        0.2,
//        0.03,
//        0.06,
//        1.0,
//        0.07,
//        0.05,
//        0.5,
//        1.0
//)
//
//object TestParams : Params(
//        32,
//        0.03,
//        0.06,
//        0.1,
//        0.15,
//        1.0,
//        0.1,
//        0.0,//0.05,
//        0.5,
//        1.0
//)
//
