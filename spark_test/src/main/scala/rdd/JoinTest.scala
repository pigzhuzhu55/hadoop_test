package rdd

import org.apache.spark.{SparkConf, SparkContext}

object JoinTest {
  def main(args: Array[String]): Unit = {

    val conf = new SparkConf().setMaster("local[2]").setAppName("joinTest")
    val sc = new SparkContext(conf)

    val rdd1 = sc.parallelize(List("dog", "salmon", "salmon", "rat", "elephant"), 3)
    val rdd2 = rdd1.keyBy(_.length)

    val rdd3 = sc.parallelize(List("dog","cat","gnu","salmon","rabbit","turkey","wolf","bear","bee"), 3)
    val rdd4 = rdd3.keyBy(_.length)

    rdd2.collect()
    rdd4.collect()

    rdd2.join(rdd4).collect()

    rdd2.leftOuterJoin(rdd4).collect()
  }
}
