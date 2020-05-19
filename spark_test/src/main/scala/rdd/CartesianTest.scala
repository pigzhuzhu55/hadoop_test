package rdd

import org.apache.spark.{SparkConf, SparkContext}

object CartesianTest {
  def main(args: Array[String]): Unit = {

    val conf = new SparkConf().setMaster("local[2]").setAppName("cartesianTest")
    val sc = new SparkContext(conf)

    val rdd1 = sc.parallelize(List(1, 2, 3, 4))
    val rdd2 = sc.parallelize(List(6, 7, 8, 9))

    val rdd3 = rdd1.cartesian(rdd2).collect().foreach(println)
  }
}
