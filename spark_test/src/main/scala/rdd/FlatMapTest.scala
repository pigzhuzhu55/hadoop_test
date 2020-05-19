package rdd

import org.apache.spark.{SparkConf, SparkContext}

object FlatMapTest {
  def main(args: Array[String]): Unit = {

    val conf = new SparkConf().setMaster("local[2]").setAppName("flatMapTest")
    val sc = new SparkContext(conf)

    val list1 = 1 to 10 toList
    val list2 = 11 to 20 toList

    val rdd1 = sc.parallelize(List(list1, list2))
    rdd1.foreach(println)
    rdd1.flatMap(x => x).collect().foreach(println)
    rdd1.flatMap(x => x.map(y => y * 2)).collect().foreach(println)
  }
}
