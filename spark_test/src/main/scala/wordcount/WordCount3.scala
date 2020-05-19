package wordcount

import org.apache.spark.SparkConf
import org.apache.spark.sql.SparkSession
import org.apache.spark.storage.StorageLevel

import scala.io.Source

object WordCount3 {

  def main(args:Array[String]):Unit = {

    System.setProperty("hadoop.home.dir","D:\\User\\hadoop-2.9.2")

    val file  = "data/wordcount/input/wordcount.csv"
    val spark  = SparkSession
      .builder()
      .appName("wordcount3")
      .master("local[*]")
      .getOrCreate()

    //wordCountByScala(file)
    //wordCountBySparkRDD(spark,file)
    wordCountBySparkDataFrame(spark,file)
  }

  /**
   *  scala
   * @param file
   */
  def  wordCountByScala(file:String) : Unit = {

    val txt = Source
      .fromFile(file)
      .getLines()
      .toArray
      .flatMap(line=>line.split(","))
      .map(word => (word,1))
      .groupBy(_._1)
      .map(value => (value._1,value._2.map(cell=>cell._2).sum))

    println("=====================scala 实现WordCount=====================")
    txt.foreach(println)
  }

  /**
   * spark RDD
   * @param spark
   * @param file
   */
  def wordCountBySparkRDD(spark:SparkSession,file:String):Unit={

    val rdd  = spark
      .sparkContext
      .textFile(file)
      .flatMap(line=>line.split(","))
      .map(word=>(word,1))
      .reduceByKey(_+_)

    println("=====================spark RDD实现WordCount=====================")
    rdd.collect().foreach(println)
  }

  /**
   * spark DataFrame
   * @param spark
   * @param file
   */
  def wordCountBySparkDataFrame(spark:SparkSession,file:String) : Unit={

    val df = spark
      .read
      .format("CSV")
      .option("header",true)
      .load(file)
      .toDF("col1","col2","col3")

    df.show()

    /*
    +-----+----+----+
    | col1|col2|col3|
    +-----+----+----+
    |hello|  12|  男|
    |  mac|  11|  女|
    | 李磊|   3|  女|
    +-----+----+----+
     */

    df.printSchema()

    /*
    root
     |-- col1: string (nullable = true)
     |-- col2: string (nullable = true)
     |-- col3: string (nullable = true)
     */

    df.select("col1")
      .union(df.select("col2"))
      .union(df.select("col3"))
      .toDF("word")
      .groupBy("word")
      .count()
      .show(numRows = 100)
  }

}
