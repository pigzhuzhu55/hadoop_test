package rdd

import org.apache.spark.{SparkConf, SparkContext}

object GroupTest {
  def main(args: Array[String]): Unit = {

    val conf = new SparkConf().setMaster("local[2]").setAppName("groupTest")
    val sc = new SparkContext(conf)

    val rdd1 = sc.parallelize(1 to 9, 3)

    rdd1.groupBy(x => {
      if (x % 2 == 0) "even" else "odd"
    }).collect().foreach(println)
  }
}
