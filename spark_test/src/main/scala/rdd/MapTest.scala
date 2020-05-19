package rdd

import org.apache.spark.SparkContext
import org.apache.spark.SparkConf
import scala.collection.mutable.ListBuffer

object MapTest {
  def main(args: Array[String]): Unit = {

    val conf = new SparkConf().setMaster("local[2]").setAppName("mapTest")
    val sc = new SparkContext(conf)

    val data = Seq(1, 2, 3, 4)
    val rdd1 = sc.parallelize(data).map(value => value * 2)
    rdd1.foreach(println)
    val rdd2 = sc.parallelize(data).map(value => (value, value * 2))
    rdd2.foreach(println)

    rdd1.mapPartitionsWithIndex { case (partIndex, iter) =>
      val partMap = scala.collection.mutable.Map[String, Int]()
      while (iter.hasNext) {
        val partName = s"part_$partIndex"
        if (partMap.contains(partName)) {
          partMap(partName) = partMap(partName) + 1
        } else {
          partMap(partName) = 1
        }
        iter.next()
      }
      partMap.iterator
    }.collect().foreach(println)


    //**********************************
    val rdd3 = sc.parallelize(1 to 9, 3)
    println(rdd3.getNumPartitions)
    println(rdd3.partitions)

    rdd3.mapPartitions(iter => {
      val list = ListBuffer[Int]()

      while (iter.hasNext) {
        val cur = iter.next()
        list.append(cur * 2)
      }

      list.toIterator
    }).collect().foreach(println)
  }
}

