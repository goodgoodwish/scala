import akka.actor._

case object ActNormalMessage
case object TryToFindSolution
case object BadGuysMakeMeAngry
case object SlapDoor

class DavidBanner extends Actor {
    import context._
    def angryState: Receive = {
        case ActNormalMessage =>
             println("01, Phew, I'm back to being David.")
             become(normalState)
        case SlapDoor =>
             println("02, Pong, door closed.")

    }
    def normalState: Receive = {
        case TryToFindSolution =>
             println("12, Looking for solution to my problem ...")
        case BadGuysMakeMeAngry =>
             println("11, I'm getting angry...")
             become(angryState)
    }
    def receive = {
        // case BadGuysMakeMeAngry => become(angryState)
        case BadGuysMakeMeAngry => become(normalState) // This is ignored
        case ActNormalMessage => become(normalState)  // ignored,
        case "123" => become(normalState)
    }
}

object BecomeHulkExample extends App {
    val system = ActorSystem("BecomeHulkExample")

    val davidBanner = system.actorOf(Props[DavidBanner], name = "DavidBanner")

    davidBanner ! "123" // init to normalState
    davidBanner ! ActNormalMessage // init to normalState, ignored,
    davidBanner ! TryToFindSolution
    davidBanner ! BadGuysMakeMeAngry
    Thread.sleep(200)
    davidBanner ! SlapDoor

    Thread.sleep(1000)
    davidBanner ! ActNormalMessage
    Thread.sleep(1000)
    davidBanner ! BadGuysMakeMeAngry
    Thread.sleep(200)
    davidBanner ! SlapDoor
    davidBanner ! TryToFindSolution // ignored
    Thread.sleep(1000)
    davidBanner ! ActNormalMessage

    Thread.sleep(200)
    davidBanner ! SlapDoor  // ignored.

    system.terminate
}
