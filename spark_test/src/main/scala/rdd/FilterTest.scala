package rdd

import org.apache.spark.{SparkConf, SparkContext}

object FilterTest {
  def main(args: Array[String]): Unit = {

    val conf = new SparkConf().setMaster("local[2]").setAppName("filterTest")
    val sc = new SparkContext(conf)

    val rdd1 = sc.parallelize(1 to 9, 3)
    rdd1.filter(x => x % 2 ==0).collect()
  }
}
