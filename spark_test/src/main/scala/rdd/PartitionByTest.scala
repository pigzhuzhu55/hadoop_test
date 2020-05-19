package rdd

import org.apache.spark.{SparkConf, SparkContext}

object PartitionByTest {
  def main(args: Array[String]): Unit = {

    val conf = new SparkConf().setMaster("local[2]").setAppName("partitionByTest")
    val sc = new SparkContext(conf)

    val rdd1 = sc.parallelize(List("dog","cat","gnu","salmon","rabbit","bee","dog","bear","bee"), 3)
    val rdd2 = sc.parallelize(List(1,1,1,1,1,1,1,1,1), 3)
    val rdd3 = rdd1.zip(rdd2)

    rdd3.glom.collect()
    val rdd4 = rdd3.partitionBy(new org.apache.spark.HashPartitioner(4))
    rdd4.glom.collect()

    val rdd5 = rdd3.partitionBy(new org.apache.spark.RangePartitioner(4, rdd3))
    rdd5.glom.collect()
  }
}
