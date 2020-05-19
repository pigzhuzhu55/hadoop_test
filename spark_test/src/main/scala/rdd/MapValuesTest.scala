package rdd

import org.apache.spark.{SparkConf, SparkContext}

object MapValuesTest {
  def main(args: Array[String]): Unit = {

    val conf = new SparkConf().setMaster("local[2]").setAppName("mapValuesTest")
    val sc = new SparkContext(conf)

    val rdd1 = sc.parallelize(List("dog", "tiger", "lion", "cat", "panther", "eagle"), 2)
    val rdd2 = rdd1.map(x => (x.length, x))

    rdd2.collect()
    rdd2.mapValues("#" + _ + "#").collect()

  }
}
