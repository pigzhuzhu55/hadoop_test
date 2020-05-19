package rdd

import org.apache.spark.{SparkConf, SparkContext}

object TakeSampleTest {
  def main(args: Array[String]): Unit = {

    val conf = new SparkConf().setMaster("local[2]").setAppName("takeSampleTest")
    val sc = new SparkContext(conf)

    val rdd1 = sc.parallelize(1 to 100000, 3)
    val rdd2 = rdd1.takeSample(true, 20, 0L)


  }
}
