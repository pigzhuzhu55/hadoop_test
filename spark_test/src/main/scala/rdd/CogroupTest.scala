package rdd

import org.apache.spark.{SparkConf, SparkContext}

object CogroupTest {
  def main(args: Array[String]): Unit = {

    val conf = new SparkConf().setMaster("local[2]").setAppName("cogroupTest")
    val sc = new SparkContext(conf)

    val rdd1 = sc.parallelize(List((1, "Hadoop"), (2, "Spark")))
    val rdd2 = sc.parallelize(List((1, "Java"), (2, "Scala"), (3, "Python")))
    val rdd3 = sc.parallelize(List((1, "HBase"), (2, "Hive"), (3, "Mongodb")))

    val rdd4 = rdd1.cogroup(rdd2, rdd3)
    rdd4.collect()
    rdd4.getNumPartitions
  }

}
