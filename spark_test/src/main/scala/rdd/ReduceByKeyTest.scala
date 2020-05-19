package rdd

import org.apache.spark.{SparkConf, SparkContext}

object ReduceByKeyTest {
  def main(args: Array[String]): Unit = {

    val conf = new SparkConf().setMaster("local[2]").setAppName("reduceByKeyTest")
    val sc = new SparkContext(conf)

    val rdd1 = sc.parallelize(List("dog","cat","gnu","salmon","rabbit","bee","dog","bear","bee"), 3)
    val rdd2 = sc.parallelize(List(1,1,1,1,1,1,1,1,1), 3)
    val rdd3 = rdd1.zip(rdd2)

    rdd3.collect()
    rdd3.glom.collect()

    rdd3.reduceByKey(_+_).collect()
    rdd3.reduceByKey(_+_).glom.collect()

  }

}
