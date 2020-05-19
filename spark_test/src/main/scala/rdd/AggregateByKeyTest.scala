package rdd

import org.apache.spark.{SparkConf, SparkContext}

object AggregateByKeyTest {
  def main(args: Array[String]): Unit = {

    val conf = new SparkConf().setMaster("local[2]").setAppName("aggregateByKeyTest")
    val sc = new SparkContext(conf)

    val rdd2 = sc.parallelize(List(('a',2),('b',3),('c',1),('b',1),('c',6)),3)
    rdd2.glom.collect().foreach(x=>{
      x.foreach(print);
      println("")
    })
    // 因为三个分片('a',2)   ('b',3),('c',1)  ('b',1),('c',6)
    /*

    分片一 ('a',2)的值是2，与初始值2比， 最大值还是2
    分片二 ('b',3)的值是3，与初始值2比，最大值3；('c',1) 的值是1，与初始值2比，最大值2
    分片三 ('b',1)的值是1，与初始值2比，最大值2；('c',6) 的值是6，与初始值2比，最大值6

    这样经过第一个函数math.max(_,_)后,三个分片的值分别如下
    分片一 是('a',2)
    分片二 是('b',3)('c',2)
    分片三 是('b',2)('c',6)

    _+_第二个函数、其实是对key进行分组计算，过程如下
      key 为 'a' 的 ('a',2) 值进行相加 ，结果 ('a',2)
      key 为 'b' 的 ('b',3)('b',2) 值进行相加 ，结果 ('b',5)
      key 为 'c' 的 ('c',2)('c',6) 值进行相加 ，结果 ('c',8)
     */
    
     rdd2.aggregateByKey(2)(math.max(_,_),_+_).foreach(println)

  }
}
