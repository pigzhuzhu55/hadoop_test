package rdd

import org.apache.spark.{SparkConf, SparkContext}

object SampleTest {
  def main(args: Array[String]): Unit = {

    val conf = new SparkConf().setMaster("local[2]").setAppName("sampleTest")
    val sc = new SparkContext(conf)

    val rdd1 = sc.parallelize(1 to 100000, 3)
    val rdd2 = rdd1.sample(false, 0.1, 0L)

    rdd2.count()
    rdd2.take(10)
  }
}
