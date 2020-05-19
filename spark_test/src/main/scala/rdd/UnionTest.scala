package rdd

import org.apache.spark.{SparkConf, SparkContext}

object UnionTest {
  def main(args: Array[String]): Unit = {

    val conf = new SparkConf().setMaster("local[2]").setAppName("unionTest")
    val sc = new SparkContext(conf)

    val rdd1 = sc.parallelize(1 to 3, 1)
    val rdd2 = sc.parallelize(5 to 7, 1)

    rdd1.collect()
    rdd2.collect()

    val rdd3 = rdd1.union(rdd2)

    rdd3.collect()

    rdd3.glom.collect()

    rdd3.getNumPartitions
  }
}
