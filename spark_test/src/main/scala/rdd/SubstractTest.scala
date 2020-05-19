package rdd

import org.apache.spark.{SparkConf, SparkContext}

object SubstractTest {
  def main(args: Array[String]): Unit = {

    val conf = new SparkConf().setMaster("local[2]").setAppName("substractTest")
    val sc = new SparkContext(conf)

    val rdd1 = sc.parallelize(1 to 9, 3)
    val rdd2 = sc.parallelize(1 to 2, 3)
    rdd1.subtract(rdd2).collect()
  }
}
