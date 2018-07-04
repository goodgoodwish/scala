package web

// import scala.concurrent.Future

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import akka.http.scaladsl.server.Directives._
import akka.stream.ActorMaterializer
import scala.io.StdIn

object Web extends App {

  implicit val system = ActorSystem("yi-system")
  implicit val materializer = ActorMaterializer()
  implicit val executionContext = system.dispatcher

  val route =
    path("hello") {
      get {
        complete(HttpEntity(ContentTypes.`text/html(UTF-8)`, "<h1>Say hello to akka-http. Yi </h1>"))
      }
    } ~
    path("") {  // matches (/) root,
      complete(HttpEntity(ContentTypes.`text/html(UTF-8)`,"<h1>Welcome to akka-http. - Yi </h1>"))
    } ~
    path("ios") {
      complete(ReturnData.ios)
    } ~
    path("gp") {
      complete(ReturnData.gp)
    }

  import akka.http.scaladsl.model.HttpMethods._
  val requestHandler: HttpRequest => HttpResponse = {

    case HttpRequest(GET, Uri.Path("/"), _, _, _) =>
      HttpResponse(entity = HttpEntity(
        ContentTypes.`text/html(UTF-8)`,
        "<html><body>Hello world!</body></html>"))

    case HttpRequest(GET, Uri.Path("/ping"), _, _, _) =>
      HttpResponse(entity = "PONG!")

    case HttpRequest(GET, Uri.Path("/ios"), _, _, _) =>
      HttpResponse(entity = ReturnData.ios)
    case HttpRequest(GET, Uri.Path("/gp"), _, _, _) =>
      HttpResponse(entity = ReturnData.gp)

    case HttpRequest(GET, Uri.Path("/crash"), _, _, _) =>
      sys.error("BOOM!")

    case r: HttpRequest =>
      r.discardEntityBytes() // important to drain incoming HTTP Entity stream
      HttpResponse(404, entity = "Unknown resource!")
  }

  val bindingFuture = Http().bindAndHandle(route, "localhost", 8080)
  // val bindingFuture = Http().bindAndHandleSync(requestHandler, "localhost", 8080)
  println(s"Server online at http://localhost:8080/  Press RETURN to stop...")

  var userInput = StdIn.readLine() // let it run until user presses return
  bindingFuture
    .flatMap(_.unbind()) // trigger unbinding from the port
    .onComplete(_ => system.terminate()) // and shutdown when done

}
