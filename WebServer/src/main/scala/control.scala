package web

import scala.util.{Try, Success, Failure}
import java.io.{FileNotFoundException, IOException}

// Automatically closing the resource,
object Control {
  def using[A <: { def close(): Unit }, B](resource: A)(f: A => B): B = {
    try {
      f(resource)
    } finally {
      resource.close()
    }
  }
}

object FileUtl {
  import Control._

  def readTextFileWithTry(filename: String): Try[List[String]] = {
    Try {
      using(io.Source.fromFile(filename)) { source =>
        source.getLines.toList
      }
    }
  }

  def fileStringFromTry(filename: String): String = {
    val aFile = readTextFileWithTry(filename)
    aFile match {
      case Success(lines) => lines.mkString
      case Failure(s) => s"Failed, message is: $s"
    }
  }

  def readTextFileWithOption(filename: String): Option[List[String]] = {
    try {
      val lines = using(io.Source.fromFile(filename)) { source =>
        (for (line <- source.getLines) yield line).toList
      }
      Some(lines)
    } catch {
      case e: FileNotFoundException => { println(s"Could not find file: $filename"); None }
      case e: IOException => { println("Got an IOException!"); None }
      case e: Exception => None
    }
  }
  def fileStringFromOption(filename: String): String = {
    val aFile = readTextFileWithOption(filename)
    aFile match {
      case Some(lines) => lines.mkString
      case None => s"Could not read file."
    }
  }

}

object ReturnData {
  def ios:String = {
    val jsonFile = "src/main/resources/rank_ios.json"
    FileUtl.fileStringFromTry(jsonFile)
  }
  def gp:String = {
    val jsonFile = "/Users/charliezhu/app/scala/WebServer/src/main/resources/rank_gp.json"
    FileUtl.fileStringFromOption(jsonFile)
  }

  def iosBasic:String = {
    val jsonFile = "/Users/charliezhu/app/scala/WebServer/src/main/resources/rank_ios.json"
    Control.using(io.Source.fromFile(jsonFile)) { source =>
      source.mkString 
    }
  }
}
