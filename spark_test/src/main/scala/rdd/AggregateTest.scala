package rdd

import org.apache.spark.{SparkConf, SparkContext}

object AggregateTest {
  def main(args: Array[String]): Unit = {

    val conf = new SparkConf().setMaster("local[2]").setAppName("aggregateTest")
    val sc = new SparkContext(conf)

    val rdd1 = sc.parallelize(List(2, 5, 8, 1, 2))
    // 因为只有一个分片，所以只执行前面那个_+_函数
    /*
    计算过程
    0+2=2
    2+5=7
    7+8=15
    15+1=16
    16+2=18
     */
    println(rdd1.aggregate(0)(_+_,_+_))


    val rdd2 = sc.parallelize(List(2, 3, 5, 1, 2),3)
    rdd2.glom.collect().foreach(x=>{
      x.foreach(print);
      println("")
    })
    // 因为只有三个分片(2)(3,5)(1,2)，所以_+_,_*_都分别执行
    /*
    _+_计算过程
    分片一 1 + 2 = 3
    分片二 1 + 3 + 5 = 9
    分片三 1 + 1 + 2 = 4

    _*_计算过程
     1 * 3 * 9 * 4 = 108
     */
    println(rdd2.aggregate(1)(_+_,_*_))

  }
}
