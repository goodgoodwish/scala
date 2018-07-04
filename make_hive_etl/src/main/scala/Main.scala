import java.io._

object Build_ETL_Main extends App {
  println("Goal, Convert Hive query to ETL INSERT SQL. \n")
  val hive_header = """set hive.exec.parallel=true;
set hive.exec.max.dynamic.partitions.pernode=5000;
set hive.exec.max.dynamic.partitions=5000;
set hive.exec.compress.output=true;
set mapreduce.output.compress=true;
set mapreduce.compress.map.output=true;
set io.seqfile.compression.type=BLOCK;
set hive.merge.mapredfiles=true;
set hive.merge.mapfiles=true;
set hive.merge.size.per.task=256000000;
set hive.merge.smallfiles.avgsize=90000000;

"""

  val folderName = "/Users/charliezhu/work/bi/sql_to_hive"
  val files = dirSQLFiles(new File(folderName))
  println(s"files class: ${files.getClass}")
  files.foreach{ file =>
    println(s"\n ${file.getPath}/ :  ${file.getName} \n")
    processFile(file.toString)
  }

  def dirSQLFiles(dir: File): List[File] =
    dir.listFiles.filter(_.isFile).toList.filter{ file =>
      file.getName.endsWith("sql")
    }
  
  def insertSQL(filename: String): String = {
    val fileHandle = new File(filename)
    val tablename = fileHandle.getName().split(".sql")(0)
    s"INSERT OVERWRITE TABLE report.$tablename" + " PARTITION(partition='${hiveconf:ds}') \n"
  }

  def processFile(inFileName: String): Unit = {
    import scala.io.Source
    import sys.process._ 

    val outFileName = inFileName.substring(0, inFileName.length - 4) + ".hql"
    val outFile = new File(outFileName)
    val bw = new BufferedWriter(new FileWriter(outFile))
    bw.write(hive_header)
    bw.write(insertSQL(inFileName))

    val bufferedSource = Source.fromFile(inFileName)
    val lines = bufferedSource.getLines
      .filter(_.indexOf("SET hive.cli.print.header=true") < 0)

    for (line <- lines) {
      val newStr = findLine(line)
      bw.write(newStr)
    }
    bw.close
    bufferedSource.close

    val cpFile = "cp " + outFileName + " /Users/charliezhu/git/bi-cloud/hive/_hive/reports/main/"
    cpFile.!
    println(cpFile)
  }

  def findLine(sqlLine: String): String = {
    val datePattern = """>=[\s]*'2018-[\d]+-[\d]+'""".r 
    val match1 = datePattern.findFirstIn(sqlLine)
    match1 match {
      case Some(s) => {
        println(s"Found: $s")
        val newDateStr = " = '\\${hiveconf:ds}'"
        val newLine = datePattern.replaceAllIn(sqlLine, newDateStr)
        println(s"Changed to: \n ${newLine}")
        newLine + "\n"
      }
      case None => s"${sqlLine}\n"
    }
  }

}
