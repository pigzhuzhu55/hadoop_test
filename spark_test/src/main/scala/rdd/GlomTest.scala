package rdd

import org.apache.spark.{SparkConf, SparkContext}

object GlomTest {
  def main(args: Array[String]): Unit = {

    val conf = new SparkConf().setMaster("local[2]").setAppName("glomTest")
    val sc = new SparkContext(conf)

    val rdd1 = sc.parallelize(1 to 100, 3)

    rdd1.collect().foreach(println)

    rdd1.glom.collect().foreach(iter => {
        iter.foreach(println)
    })

  }
}
