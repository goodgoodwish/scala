package web

import scala.concurrent.{Future, Await}
import scala.util.{Try, Success, Failure}

object Web extends App {
  println("start demo")
  println(WebService.getDataAkkaHTTP)
  // WebService.getDataApache
  // DBService.queryUser
}

case class User(username: String, friends: Int, enemies: Int, isAlive: Boolean)

object WebService {

  import ujson.Js

  import akka.actor.ActorSystem
  import akka.http.scaladsl.Http
  import akka.http.scaladsl.model._
  import akka.stream.ActorMaterializer
  import akka.util.ByteString

  def getDataAkkaHTTP: Unit = {

    implicit val system = ActorSystem()
    implicit val materializer = ActorMaterializer()
    // needed for the future flatMap/onComplete in the end
    implicit val executionContext = system.dispatcher

    val url = "http://localhost:8080/gp"
    val responseFuture: Future[HttpResponse] = Http().singleRequest(HttpRequest(uri = url))

    // Future-Based Variant,
    responseFuture.onComplete {
      case Success(result) => {
        val HttpResponse(statusCodes, headers, entity, _) = result
        entity.dataBytes.runFold(ByteString(""))(_ ++ _).foreach (body => println("body class: ", body.utf8String.getClass))
        val contentF = entity.dataBytes.runFold(ByteString(""))(_ ++ _).map(_.utf8String)
        contentF.onComplete {
          case Success(res) => {
            println(res.substring(0,5))  // String
            DBService.dbRun(res)
          }
          case Failure(_) => sys.error("something wrong")
        }
        println(contentF.getClass)
        system.terminate()
      }
      case Failure(_) => sys.error("something wrong")
    }

  }

  def getDataPlayWS:Unit = {

    var content = ""
    // val url = "https://api.appannie.com/v1.2/accounts"
    // val httpGet = new HttpGet(url)

    println(content)
    
  }

  import org.apache.http.client.methods.HttpGet
  import org.apache.http.impl.client.HttpClientBuilder
  import org.apache.http.impl.client.CloseableHttpClient

  def getDataApache:Unit = {

    var content = ""
// https://api.appannie.com
// /v1.2/apps/{market}/{asset}/{product_id}/ranks?start_date={start_date}&end_date={end_date}&interval={interval}&countries={countries}&category={categories}&feed={feeds}&device={iphone|ipad|mac|android}
// /v1.2/apps/ios/app/1140577358/ranks?start_date=2018-05-02&end_date=2018-05-02
// /v1.2/apps/ios/app/660004961/ranks?start_date=2017-08-20&end_date=2017-08-20

    // val url = "https://api.appannie.com//v1.2/apps/ios/app/1140577358/ranks?start_date=2018-05-05&end_date=2018-05-06"
    val url = "https://api.appannie.com//v1.2/apps/google-play/app/20600005988709/ranks?start_date=2018-05-05&end_date=2018-05-06"
    val httpGet = new HttpGet(url)
    val authKey = "read from OS env"

    httpGet.setHeader("Authorization", authKey)
    val client: CloseableHttpClient = HttpClientBuilder.create().build()
    val res = client.execute(httpGet)
    val entity = res.getEntity
    if (entity != null) {
      val inputStream = entity.getContent
      content = io.Source.fromInputStream(inputStream).getLines.mkString
      inputStream.close
    }
    client.close

    // val json = ujson.read(content)
    println(content)
    
  }

  def parseJSON:Unit = {

    var content = """
{
  "page_index": 0,
  "accounts": [
    {
      "account_id": 101,
      "account_name": "Apple"
    },
    {
      "account_id": 102,
      "account_name": "Google"
    }
  ]
}"""

    val json = ujson.read(content)
    println(json("accounts"))
    json("accounts").arr.foreach { x =>
      println(x("account_id"))
    }
    
  }

  def getHeader = {
    // val get = new HttpGet("http://alvinalexander.com/")
    val get = new HttpGet("http://localhost:9001/abc")
    val client = HttpClientBuilder.create().build()
    val response = client.execute(get)
    response.getAllHeaders.foreach(header => println(header))
    client.close
  }
}

object JsonUtl {
  def parseToList:Unit = {
    println("start")
  }

  def addDatabase:Unit = ???
}


// import jdbcProfile.api._
// import slick.driver.PostgresDriver.simple._
import slick.jdbc.PostgresProfile.api._
import scala.concurrent.ExecutionContext.Implicits.global
import java.sql.Date

object DBService {

  class Users(tag: Tag) extends Table[(Int, String)](tag, "users") {
    def id = column[Int]("id", O.PrimaryKey)
    def username = column[String]("username")
    def * = (id, username)
  }

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
    // queryRank(db)
    db.close
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
    // Await setupFuture

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
  }

  def queryUser:Unit = {
    // val connectionUrl = "jdbc:postgresql://localhost/scala_db"
    // val db = Database.forURL(connectionUrl, user = "scala_user", driver = "org.postgresql.Driver") 
    val connectionUrl = "jdbc:postgresql://localhost/scala_db"
    val db = Database.forURL(connectionUrl, driver = "org.postgresql.Driver") 
    
    val users = TableQuery[Users]

    println("Users:")

    val userFuture = db.run(users.result)

    userFuture.onComplete {
      case Success(res) => println(res)
      case Failure(s) => s"error $s"
    }

    userFuture.foreach {
      result => result.foreach(x => println(x._1))
    }

    userFuture.foreach {
      result => result.foreach{
        case(id, username) => println("print:", id, username)
      }
    }

    userFuture.map(_.filter(_._1 > 1).foreach{
      case(id, username) => println("print filter, id > 1:", id, username)
    })

    db.run(users.result).map(_.foreach{
      case(id, username) => println("print map:", id, username)
    })

    // for comprehension 
    val q1 = for {
      u <- users
    } yield (u.id, u.username)

    val user_stream = db.stream(q1.result)

    user_stream.foreach{
      case(id, username) => println("for stream:", id, username)
    }

    // db.close
    
  }

  def addUser:Unit = {
    val connectionUrl = "jdbc:postgresql://localhost/scala_db"
    val db = Database.forURL(connectionUrl, driver = "org.postgresql.Driver") 
    
    val users = TableQuery[Users]
    val setup = DBIO.seq(
      users ++= Seq(
        (3, "Tonny"),
        (4, "John 3")
      )
    ) 

    val setupFuture = db.run(setup)

    db.close

  }

  def printTuple: Unit = {
    val t1 = Vector((1,"A"), (2, "B"))
    t1.foreach{
      case(id, name) => println(id, name)
    }
    t1.foreach(x => println(x))
    // t1.foreach(case(id, name) => println(id, name))

    // Function or Block  v.s.  Expression,
    t1.foreach( x => 
      x match {case(id, name) => println(id, name)}
    )
    // t1.foreach( x => 
    //   x match (case(id, name) => println(id, name))
    // )
  }

}
