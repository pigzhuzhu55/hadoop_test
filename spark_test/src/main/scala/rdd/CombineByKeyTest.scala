package rdd

import org.apache.spark.{SparkConf, SparkContext}

object CombineByKeyTest {
  def main(args: Array[String]): Unit = {

    val conf = new SparkConf().setMaster("local[2]").setAppName("combineByKeyTest")
    val sc = new SparkContext(conf)

    val rdd1 = sc.parallelize(List("dog","cat","gnu","salmon","rabbit","turkey","wolf","bear","bee"), 3)
    val rdd2 = sc.parallelize(List(1,1,2,2,2,1,2,2,2), 3)

    val rdd3 = rdd2.zip(rdd1)
    rdd3.collect()
    rdd3.glom.collect()

    val rdd4 = rdd3.combineByKey(List(_), (x: List[String], y:String) => y::x, (x: List[String], y:List[String]) => x ::: y)
    rdd4.collect()
  }
}
