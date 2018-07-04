import akka.actor.Actor
import akka.actor.ActorSystem
import akka.actor.Props

import actortests.parentchild._

class HelloActor extends Actor { def receive = {
  case "hello" => println("hello back at you")
  case _ => println("huh? What? 7 8 9  ") }
}

object Main extends App {

  println("=============")
  // an actor needs an ActorSystem
  val system = ActorSystem("HelloSystem") // create and start the actor
  val helloActor = system.actorOf(Props[HelloActor], name = "helloactor")
  // send the actor two messages
  helloActor ! "hello"
  helloActor ! "buenos dias"
  // shut down the system
  system.terminate
}

