package rdd

import org.apache.spark.{SparkConf, SparkContext}

object DistinctTest {

  def main(args: Array[String]): Unit = {

    val conf = new SparkConf().setMaster("local[2]").setAppName("distinctTest")
    val sc = new SparkContext(conf)

    val rdd1 = sc.parallelize(List(1, 2, 2, 3, 4, 5, 5, 6))

    rdd1.distinct().collect()
  }
}
