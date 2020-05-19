package wordcount

import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.sql.SparkSession
import org.apache.spark.storage.StorageLevel

object WordCount {

  def main(args: Array[String]): Unit = {

    /*
    请在 VM options 设置 -Dspark.master=local[*]
     */
    /*
    如果是打包完，然后spark submit到standlone上运行，先在hdfs上上传本例子的输入文件：1.txt
    hadoop fs -mkdir -p /user/root/data/wordcount/input
    hadoop fs -put  /usr/cai/1.txt /user/root/data/wordcount/input
     */


    val spark = SparkSession
      .builder()
      .appName("WordCount")
      .getOrCreate()


    val rdd = spark
      .sparkContext
      .textFile("data/wordcount/input/1.txt")
      .flatMap(line => line.split(" "))
      .map(word => (word, 1))
      .reduceByKey(_ + _)
      .persist(StorageLevel.DISK_ONLY)

    rdd.collect().foreach(println)

  }
}
