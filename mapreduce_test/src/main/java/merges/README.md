<a href="https://github.com/pigzhuzhu55/hadoop_test">返回目录</a>

## 02-小文件合并

- 输入日志目录的文件，包含.txt，.svn等很多杂乱无章的文件，从中过滤出.txt文件，并按日志重新归类存档到hdfs

-- 具体规则看代码

-- 本例结果是存入hdfs上
> 查看日志整理结果信息
```html
hadoop fs -ls /merges  

hadoop fs -cat /merges/20200202.txt
hadoop fs -cat /merges/20200203.txt
```

<a href="../casemax">下一练习</a>