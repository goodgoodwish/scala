package web

import scala.concurrent.{Future, Await}
import scala.util.{Try, Success, Failure}
import ujson.Js

object Web extends App {
  println("start demo")
  // WebService.akkaClient
  WebService.getDataAkkaHTTP
  // WebService.getDataApache
  // DBService.queryUser
}

case class User(username: String, friends: Int, enemies: Int, isAlive: Boolean)

object WebService {

  import akka.actor.ActorSystem
  import akka.http.scaladsl.Http
  import akka.http.scaladsl.model._
  import akka.stream.ActorMaterializer
  import akka.util.ByteString
  import HttpMethods._  // GET   HEAD   OPTIONS   PATCH   POST   PUT ...
  import akka.http.scaladsl.model.headers.RawHeader

  def akkaClient: Unit = {
    implicit val system = ActorSystem()
    implicit val materializer = ActorMaterializer()
    implicit val executionContext = system.dispatcher

    val dataUrl = "http://localhost:8080/gp"
    val responseFuture: Future[HttpResponse] = Http().singleRequest(
      HttpRequest(GET, uri = dataUrl)
        .withHeaders(
          RawHeader("APIKEY", "token2============")
        )
    )

    responseFuture
      .onComplete {
        case Success(res) => {
          println(res)
          system.terminate()
        }
        case Failure(_)   => sys.error("something wrong")
      }
  }

  def getDataAkkaHTTP: Unit = {

    implicit val system = ActorSystem()
    implicit val materializer = ActorMaterializer()
    implicit val executionContext = system.dispatcher

    val url = "http://localhost:8080/gp"
    val responseFuture: Future[HttpResponse] = Http().singleRequest(HttpRequest(uri = url))

    // Future-Based Variant,
    responseFuture.onComplete {
      case Success(HttpResponse(StatusCodes.OK, headers, entity, _)) => {
        entity.dataBytes.runFold(ByteString(""))(_ ++ _).foreach { body => 
          println("body class: ", body.utf8String.getClass)
          DBService.dbRun(body.utf8String)
        }
        system.terminate()
      }
      case Success(HttpResponse(code, _, _, _)) => {
        println("Request failed, response code: " + code)
        system.terminate()
      }
      case Failure(ex: Exception) => {
            println("http request error: "+ex.getMessage)
            sys.error("something wrong")
            system.terminate()
      }
    }
  }
}

import slick.jdbc.PostgresProfile.api._
import scala.concurrent.ExecutionContext.Implicits.global
import java.sql.Date

object DBService {

  class RankHistory(tag: Tag) extends Table[(String, Date, Int)](tag, "rank_history_appannie") {
    def country_name = column[String]("country_name")
    def rank_date = column[Date]("rank_date")
    // def rank_type = column[String]("rank_type")
    def rank_num = column[Int]("rank_num")
    def * = (country_name, rank_date, rank_num)
  }

  def dbRun(strData:String): Unit = {
    val db = connectDB
    addRank(db, strData)
    queryRank(db)
    // Control.using(connectDB){ 
    //   db => {
    //     addRank(db, strData)
    //     queryRank(db)
    //   }
    // }
  }

  def connectDB:slick.jdbc.PostgresProfile.backend.DatabaseDef = {
    // val db = Database.forURL(connectionUrl, user = "scala_user", driver = "org.postgresql.Driver") 
    val connectionUrl = "jdbc:postgresql://localhost/scala_db"
    Database.forURL(connectionUrl, driver = "org.postgresql.Driver") 
  }

  def addRank(db: slick.jdbc.PostgresProfile.backend.DatabaseDef, strData:String):Unit = {
    val rank = TableQuery[RankHistory]
    val json = ujson.read(strData)

    json("product_ranks").arr.take(2).map{ x => 
      println("----")
      x("ranks").obj.keys.foreach(println) 
    }

    val rows = json("product_ranks").arr.take(2).map{ x =>
        x("ranks").obj.keys.map{ y =>
          (x("country").str, java.sql.Date.valueOf(y), x("ranks")(y).num.toInt)
        }
    }.flatten
    println("rows class: ", rows.getClass)
    println(rows)

    val setup = DBIO.seq(
      rank ++= rows
    )

    // val setup = DBIO.seq(
    //   rank ++= Seq(
    //     ("a", "a", 1),
    //     ("b", "b", 2)
    //   )
    // ) 

    val setupFuture = db.run(setup)

  }

  def queryRank(db: slick.jdbc.PostgresProfile.backend.DatabaseDef):Unit = {
    val rank = TableQuery[RankHistory]
    val rankFuture = db.run(rank.result)
    println("Rank: ")

    rankFuture.onComplete {
      case Success(res) => println(res)
      case Failure(s) => s"error $s"
    }

    rankFuture.map(_.filter(_._3 > 100).foreach{
      case(_, rank_date, rank_num) => println("print filter, rank > 1:", rank_date, rank_num)
    })

    // for comprehension 
    val q1 = for {
      r <- rank
    } yield (r.rank_date, r.rank_num)
    val a = q1.result
    // val f: Future[Seq[(Date, Int)]] = db.run(a)

    val p:slick.basic.DatabasePublisher[(java.sql.Date, Int)] = db.stream(a)

    p.foreach{
      case(rank_date, rank_num) => println("for stream:", rank_date, rank_num)
    }

    // clean up, close db, after stream finish.
    // book, http://books.underscore.io/essential-slick/essential-slick-3.html#andfinally-and-cleanup
    val action = a.cleanUp {
      case Some(err) => {
        print(err)
        db.close
        DBIO.successful(0)
      }
      case None => {
        db.close
        DBIO.successful(0)
      }
    }

  }

}

object Control {
  def using[A <: { def close(): Unit }, B](resource: A)(f: A => B): B = {
    try {
      f(resource)
    } finally {
      resource.close()
    }
  }
}
