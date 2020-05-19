<a href="https://github.com/pigzhuzhu55/hadoop_test">返回目录</a>
##
### map
> 初始化一个Seq对象`data`。
```scala
    val data = Seq(1, 2, 3, 4)
```

> 通过调用SparkContext的parallelize方法，集合的对象`data`将会被拷贝，创建出一个可以被并行操作的分布式数据集`rdd1` 。  
```scala
     val rdd1 = sc.parallelize(data)
```

> 对RDD中的每个元素都执行一个指定的函数类（映射）产生一个新的RDD。
```scala
     val rdd1 = sc.parallelize(data).map(value => value * 2)
```
*任何原RDD中的元素在新RDD中都有且只有一个元素与之对应。*
> 当然map也可以把Key元素变成Key-Value对。
```scala
  val rdd2 = sc.parallelize(data).map(value => (value, value * 2))
```

### mapPartitions
+ map是对RDD中的每一个元素进行操作，而mapPartitions则是对RDD中的每个分区的迭代器进行操作。
+ mapPartitions效率比map高的多。
+ mapPartitions函数获取到每个分区的迭代器，在函数中通过这个分区整体的迭代器对整个分区的元素进行操作。

> 把示例代码中的 `local[2]` 改成 `local[4]`,mapPartitions运行的结果会不一样
```scala
    val conf = new SparkConf().setMaster("local[4]").setAppName("maptest")
    val sc = new SparkContext(conf)
    ...
```

+ mapPartitions优点  
    如果是普通的map，比如一个partition中有1万条数据。ok，那么你的function要执行和计算1万次。  
    使用MapPartitions操作之后，一个task仅仅会执行一次function，function一次接收所有的partition数据。  
    只要执行一次就可以了，性能比较高。如果在map过程中需要频繁创建额外的对象(例如将rdd中的数据通过jdbc写入数据库,map需要为每个元素创建一个链接而mapPartition为每个partition创建一个链接),则mapPartitions效率比map高的多。  
    SparkSql或DataFrame默认会对程序进行mapPartition的优化。  
+ mapPartitions缺点  
    如果是普通的map操作，一次function的执行就处理一条数据；那么如果内存不够用的情况下， 比如处理了1千条数据了，那么这个时候内存不够了，那么就可以将已经处理完的1千条数据从内存里面垃圾回收掉，或者用其他方法，腾出空间来吧。 
    所以说普通的map操作通常不会导致内存的OOM异常。  
    但是MapPartitions操作，对于大量数据来说，比如甚至一个partition，100万数据， 一次传入一个function以后，那么可能一下子内存不够，但是又没有办法去腾出内存空间来，可能就OOM，内存溢出。  

### flatMap
> 将原来RDD中的每个元素通过函数f转换成新的元素，并将生成的RDD的每个集合中的元素合并为一个集合。
```scala
val rdd1 = sc.parallelize(List(list1, list2))
rdd1.flatMap(x => x).collect().foreach(println)
```

### glom
> 将分区元素转换成数组。

### union
> 相同数据类型RDD进行合并，并不去重

### cartesian
> 对RDD内所有的元素进行笛卡尔积操作

### groupBy
> 将元素通过函数生成相应的 Key，数据转化为 Key-Value 格式，之后将Key 相同的元素分为一组。

### filter
> 对RDD元素进行过滤

### distinct
> 对RDD中的元素去重操作

### substract
> RDD间进行减操作，去除相同数据元素（去掉含有重复的项）

### sample
> 对RDD元素进行采样操作，获取所有元素的子集（按照比例随机抽样）

### takeSample
> 上面的sample函数是一个原理，不同的是不使用相对比例采样，而是按设定的采样个数进行采样


##
### mapValues
> (对Value值进行变换)原RDD中的Key保持不变，与新的Value一起组成新的RDD中的元素。因此，该函数只适用于元素为KV对的RDD。即针对（Key， Value）型数据中的 Value 进行 Map 操作，而不对 Key 进行处理。  
```shell script
scala> val rdd1 = sc.parallelize(List("dog", "tiger", "lion", "cat", "panther", "eagle"), 2)
rdd1: org.apache.spark.rdd.RDD[String] = ParallelCollectionRDD[48] at parallelize at <console>:28

scala> val rdd2 = rdd1.map(x => (x.length, x))
rdd2: org.apache.spark.rdd.RDD[(Int, String)] = MapPartitionsRDD[49] at map at <console>:30

scala> rdd2.collect()
res44: Array[(Int, String)] = Array((3,dog), (5,tiger), (4,lion), (3,cat), (7,panther), (5,eagle))

scala> rdd2.mapValues("#" + _ + "#").collect()
res46: Array[(Int, String)] = Array((3,#dog#), (5,#tiger#), (4,#lion#), (3,#cat#), (7,#panther#), (5,#eagle#))

```

### combineByKey
> (按key聚合)相当于将元素为 (Int， Int) 的 RDD 转变为了 (Int， Seq[Int]) 类型元素的 RDD。
```shell script
scala> val rdd1 = sc.parallelize(List("dog","cat","gnu","salmon","rabbit","turkey","wolf","bear","bee"), 3)
rdd1: org.apache.spark.rdd.RDD[String] = ParallelCollectionRDD[52] at parallelize at <console>:28

scala> val rdd2 = sc.parallelize(List(1,1,2,2,2,1,2,2,2), 3)
rdd2: org.apache.spark.rdd.RDD[Int] = ParallelCollectionRDD[53] at parallelize at <console>:28

scala> val rdd3 = rdd2.zip(rdd1)
rdd3: org.apache.spark.rdd.RDD[(Int, String)] = ZippedPartitionsRDD2[54] at zip at <console>:32

scala> rdd3.collect()
res47: Array[(Int, String)] = Array((1,dog), (1,cat), (2,gnu), (2,salmon), (2,rabbit), (1,turkey), (2,wolf), (2,bear), (2,bee))

scala> rdd3.glom.collect()
res49: Array[Array[(Int, String)]] = Array(Array((1,dog), (1,cat), (2,gnu)), Array((2,salmon), (2,rabbit), (1,turkey)), Array((2,wolf), (2,bear), (2,bee)))

scala> val rdd4 = rdd3.combineByKey(List(_), (x: List[String], y:String) => y::x, (x: List[String], y:List[String]) => x ::: y)
rdd4: org.apache.spark.rdd.RDD[(Int, List[String])] = ShuffledRDD[55] at combineByKey at <console>:34

scala> rdd4.collect()
res48: Array[(Int, List[String])] = Array((1,List(cat, dog, turkey)), (2,List(gnu, rabbit, salmon, bee, bear, wolf)))
```

### reduceByKey
> reduceByKey 是比 combineByKey 更简单的一种情况，只是两个值合并成一个值，即相同的key合并value。
```shell script
scala>  val rdd1 = sc.parallelize(List("dog","cat","gnu","salmon","rabbit","bee","dog","bear","bee"), 3)
rdd1: org.apache.spark.rdd.RDD[String] = ParallelCollectionRDD[57] at parallelize at <console>:28

scala> val rdd2 = sc.parallelize(List(1,1,1,1,1,1,1,1,1), 3)
rdd2: org.apache.spark.rdd.RDD[Int] = ParallelCollectionRDD[58] at parallelize at <console>:28

scala> val rdd3 = rdd1.zip(rdd2)
rdd3: org.apache.spark.rdd.RDD[(String, Int)] = ZippedPartitionsRDD2[59] at zip at <console>:32

scala> rdd3.collect()
res50: Array[(String, Int)] = Array((dog,1), (cat,1), (gnu,1), (salmon,1), (rabbit,1), (bee,1), (dog,1), (bear,1), (bee,1))

scala> rdd3.glom.collect()
res53: Array[Array[(String, Int)]] = Array(Array((dog,1), (cat,1), (gnu,1)), Array((salmon,1), (rabbit,1), (bee,1)), Array((dog,1), (bear,1), (bee,1)))

scala> rdd3.reduceByKey(_+_).collect()
res51: Array[(String, Int)] = Array((rabbit,1), (bee,2), (gnu,1), (cat,1), (salmon,1), (bear,1), (dog,2))

scala> rdd3.reduceByKey(_+_).glom.collect()
res52: Array[Array[(String, Int)]] = Array(Array((rabbit,1), (bee,2), (gnu,1), (cat,1)), Array((salmon,1)), Array((bear,1), (dog,2)))
```


### partitionBy
> 按Key值对RDD进行重新分区操作
```shell script
scala> val rdd1 = sc.parallelize(List("dog","cat","gnu","salmon","rabbit","bee","dog","bear","bee"), 3)
rdd1: org.apache.spark.rdd.RDD[String] = ParallelCollectionRDD[66] at parallelize at <console>:28

scala> val rdd2 = sc.parallelize(List(1,1,1,1,1,1,1,1,1), 3)
rdd2: org.apache.spark.rdd.RDD[Int] = ParallelCollectionRDD[67] at parallelize at <console>:28

scala> val rdd3 = rdd1.zip(rdd2)
rdd3: org.apache.spark.rdd.RDD[(String, Int)] = ZippedPartitionsRDD2[68] at zip at <console>:32

scala> rdd3.glom.collect()
res58: Array[Array[(String, Int)]] = Array(Array((dog,1), (cat,1), (gnu,1)), Array((salmon,1), (rabbit,1), (bee,1)), Array((dog,1), (bear,1), (bee,1)))

scala> val rdd4 = rdd3.partitionBy(new org.apache.spark.HashPartitioner(4))
rdd4: org.apache.spark.rdd.RDD[(String, Int)] = ShuffledRDD[71] at partitionBy at <console>:34

scala> val rdd5 = rdd3.partitionBy(new org.apache.spark.RangePartitioner(4, rdd3))
rdd5: org.apache.spark.rdd.RDD[(String, Int)] = ShuffledRDD[79] at partitionBy at <console>:34

scala> rdd4.glom.collect()
res62: Array[Array[(String, Int)]] = Array(Array((dog,1), (dog,1), (bear,1)), Array(), Array((cat,1), (gnu,1), (salmon,1), (rabbit,1), (bee,1), (bee,1)), Array())

scala> rdd5.glom.collect()
res64: Array[Array[(String, Int)]] = Array(Array((bee,1), (bear,1), (bee,1)), Array((dog,1), (cat,1), (dog,1)), Array((gnu,1)), Array((salmon,1), (rabbit,1)))
```

### cogroup
> 按Key值聚集操作
```shell script
scala> val rdd1 = sc.parallelize(List((1, "Hadoop"), (2, "Spark")))
rdd1: org.apache.spark.rdd.RDD[(Int, String)] = ParallelCollectionRDD[95] at parallelize at <console>:28

scala> val rdd2 = sc.parallelize(List((1, "Java"), (2, "Scala"), (3, "Python")))
rdd2: org.apache.spark.rdd.RDD[(Int, String)] = ParallelCollectionRDD[96] at parallelize at <console>:28

scala> val rdd3 = sc.parallelize(List((1, "HBase"), (2, "Hive"), (3, "Mongodb")))
rdd3: org.apache.spark.rdd.RDD[(Int, String)] = ParallelCollectionRDD[97] at parallelize at <console>:28

scala> val rdd4 = rdd1.cogroup(rdd2, rdd3)
rdd4: org.apache.spark.rdd.RDD[(Int, (Iterable[String], Iterable[String], Iterable[String]))] = MapPartitionsRDD[99] at cogroup at <console>:34

scala> rdd4.collect()
res73: Array[(Int, (Iterable[String], Iterable[String], Iterable[String]))] = Array((1,(CompactBuffer(Hadoop),CompactBuffer(Java),CompactBuffer(HBase))), (2,(CompactBuffer(Spark),CompactBuffer(Scala),CompactBuffer(Hive))), (3,(CompactBuffer(),CompactBuffer(Python),CompactBuffer(Mongodb))))

scala> rdd4.getNumPartitions
res74: Int = 8
```

### join
> 按Key值连接，自然连接，输出连接键匹配的记录。
```shell script
scala> val rdd1 = sc.parallelize(List("dog", "salmon", "salmon", "rat", "elephant"), 3)
rdd1: org.apache.spark.rdd.RDD[String] = ParallelCollectionRDD[101] at parallelize at <console>:28

scala> val rdd2 = rdd1.keyBy(_.length)
rdd2: org.apache.spark.rdd.RDD[(Int, String)] = MapPartitionsRDD[102] at keyBy at <console>:30

scala> val rdd3 = sc.parallelize(List("dog","cat","gnu","salmon","rabbit","turkey","wolf","bear","bee"), 3)
rdd3: org.apache.spark.rdd.RDD[String] = ParallelCollectionRDD[103] at parallelize at <console>:28

scala> val rdd4 = rdd3.keyBy(_.length)
rdd4: org.apache.spark.rdd.RDD[(Int, String)] = MapPartitionsRDD[104] at keyBy at <console>:30

scala> rdd2.collect()
res76: Array[(Int, String)] = Array((3,dog), (6,salmon), (6,salmon), (3,rat), (8,elephant))

scala> rdd3.collect()
res77: Array[String] = Array(dog, cat, gnu, salmon, rabbit, turkey, wolf, bear, bee)

scala> rdd4.collect()
res78: Array[(Int, String)] = Array((3,dog), (3,cat), (3,gnu), (6,salmon), (6,rabbit), (6,turkey), (4,wolf), (4,bear), (3,bee))

scala> rdd2.join(rdd4).collect()
res79: Array[(Int, (String, String))] = Array((6,(salmon,salmon)), (6,(salmon,rabbit)), (6,(salmon,turkey)), (6,(salmon,salmon)), (6,(salmon,rabbit)), (6,(salmon,turkey)), (3,(dog,dog)), (3,(dog,cat)), (3,(dog,gnu)), (3,(dog,bee)), (3,(rat,dog)), (3,(rat,cat)), (3,(rat,gnu)), (3,(rat,bee)))

scala> rdd2.leftOuterJoin(rdd4).collect()
res81: Array[(Int, (String, Option[String]))] = Array((6,(salmon,Some(salmon))), (6,(salmon,Some(rabbit))), (6,(salmon,Some(turkey))), (6,(salmon,Some(salmon))), (6,(salmon,Some(rabbit))), (6,(salmon,Some(turkey))), (3,(dog,Some(dog))), (3,(dog,Some(cat))), (3,(dog,Some(gnu))), (3,(dog,Some(bee))), (3,(rat,Some(dog))), (3,(rat,Some(cat))), (3,(rat,Some(gnu))), (3,(rat,Some(bee))), (8,(elephant,None)))
```

##
### foreach
### saveAsTextFile
```shell script
scala> val rdd1 = sc.parallelize(List(1, 2, 3, 4, 5))
rdd1: org.apache.spark.rdd.RDD[Int] = ParallelCollectionRDD[65] at parallelize at <console>:28

scala> rdd1.saveAsTextFile("/tmp/spark/")
```
### collect：收集元素
```shell script
scala> val rdd1 = sc.parallelize(List("Gnu", "Cat", "Rat", "Dog", "Gnu", "Rat"), 2)
rdd1: org.apache.spark.rdd.RDD[String] = ParallelCollectionRDD[67] at parallelize at <console>:28

scala> rdd1.collect()
res64: Array[String] = Array(Gnu, Cat, Rat, Dog, Gnu, Rat)
```
### collectAsMap
> 收集key/value型的RDD中的元素
```shell script
scala> val rdd1 = sc.parallelize(List(1, 2, 1, 3), 1)
rdd1: org.apache.spark.rdd.RDD[Int] = ParallelCollectionRDD[68] at parallelize at <console>:28

scala> val rdd2 = rdd1.zip(rdd1)
rdd2: org.apache.spark.rdd.RDD[(Int, Int)] = ZippedPartitionsRDD2[69] at zip at <console>:30

scala> rdd2.collect()
res65: Array[(Int, Int)] = Array((1,1), (2,2), (1,1), (3,3))

scala> rdd2.collectAsMap()
res66: scala.collection.Map[Int,Int] = Map(2 -> 2, 1 -> 1, 3 -> 3)
```
### reduceByKeyLocally
> 实现的是先reduce再collectAsMap的功能，先对RDD的整体进行reduce操作，然后再收集所有结果返回为一个HashMap
```shell script
scala> val rdd1 = sc.parallelize(List("dog", "cats", "word", "gnu", "cats"), 2)
rdd1: org.apache.spark.rdd.RDD[String] = ParallelCollectionRDD[76] at parallelize at <console>:28

scala> val rdd2 = rdd1.map(x => (x, x.length))
rdd2: org.apache.spark.rdd.RDD[(String, Int)] = MapPartitionsRDD[77] at map at <console>:30

scala> rdd2.collect()
res73: Array[(String, Int)] = Array((dog,3), (cats,4), (word,4), (gnu,3), (cats,4))

scala> val rdd2 = rdd1.map(x => (x, 1))
rdd2: org.apache.spark.rdd.RDD[(String, Int)] = MapPartitionsRDD[78] at map at <console>:30

scala> rdd2.collect()
res74: Array[(String, Int)] = Array((dog,1), (cats,1), (word,1), (gnu,1), (cats,1))

scala> rdd2.reduceByKeyLocally(+)
res75: scala.collection.Map[String,Int] = Map(cats -> 2, word -> 1, dog -> 1, gnu -> 1)
```
### lookup
> 查找元素，对（Key，Value）型的RDD操作，搜索指定Key对应的元素
```shell script
scala> val rdd1 = sc.parallelize(List("dog", "cats", "word", "gnu", "cats"), 2)
rdd1: org.apache.spark.rdd.RDD[String] = ParallelCollectionRDD[80] at parallelize at <console>:28

scala> val rdd2 = rdd1.map(x => (x, 1))
rdd2: org.apache.spark.rdd.RDD[(String, Int)] = MapPartitionsRDD[81] at map at <console>:30

scala> rdd2.lookup("cats")
res77: Seq[Int] = WrappedArray(1, 1)
```
### top
> top(n)寻找值最大的前n个元素
```shell script
scala> val rdd1 = sc.parallelize(List("dog", "cats", "word", "gnu", "cats"), 2)
rdd1: org.apache.spark.rdd.RDD[String] = ParallelCollectionRDD[84] at parallelize at <console>:28

scala> rdd1.top(2)
res79: Array[String] = Array(word, gnu)  
```
### reduce
> 通过函数func先聚集各分区的数据集，再聚集分区之间的数据，func接收两个参数，返回一个新值，新值再做为参数继续传递给函数func，直到最后一个元素
```shell script
scala> val rdd1 = sc.parallelize(1 to 100, 3)
rdd1: org.apache.spark.rdd.RDD[Int] = ParallelCollectionRDD[86] at parallelize at <console>:28

scala> rdd1.reduce(+)
res81: Int = 5050
```
### fold：合并
> 先对rdd分区的每一个分区进行使用op函数，在调用op函数过程中将zeroValue参与计算，最后在对每一个分区的结果调用op函数，同理此处zeroValue再次参与计算！
```shell script
scala> val rdd1 = sc.parallelize(List(1, 2, 3), 1)
rdd1: org.apache.spark.rdd.RDD[Int] = ParallelCollectionRDD[89] at parallelize at <console>:28

scala> val rdd2 = sc.parallelize(List(1, 2, 3), 2)
rdd2: org.apache.spark.rdd.RDD[Int] = ParallelCollectionRDD[90] at parallelize at <console>:28

scala> val rdd3 = sc.parallelize(List(1, 2, 3), 3)
rdd3: org.apache.spark.rdd.RDD[Int] = ParallelCollectionRDD[91] at parallelize at <console>:28

scala> rdd1.fold(1)(+)
res91: Int = 8

# 1 + 2 + 3 + 1 = 7 
# 7 + 1 = 8

scala> rdd2.fold(1)(+)
res92: Int = 9
# 1 + 2 + 1 = 4 
# 3 + 1 = 4
# 4+ 4 + 1 = 9 

scala> rdd3.fold(1)(+)
res93: Int = 10
# 1 + 1 = 2 
# 2 + 1 = 3 
# 3 + 1 = 4 
# 2 + 3 + 4 + 1 = 10 
```
### aggregate
>接受多个输入，并按照一定的规则运算以后输出一个结果值  
*见代码 AggregateTest*
### aggregateByKey
>接受多个输入，并按照一定的规则运算以后分组输出结果值  
*见代码 AggregateByKeyTest*