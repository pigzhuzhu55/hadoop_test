package wordcount;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

public class WordCountMapReduce {

    public static void main(String[] args)throws Exception{

        //创建配置对象
        Configuration conf = new Configuration();

        //输入输出目录指定
        args = new String[2];
        args[0]="data/wordcount/input/";
        args[1]="data/wordcount/output/";

        //重置输出目录（删除输出目录）
        Path mypath =  new Path(args[1]);
        FileSystem hdfs = mypath.getFileSystem(conf);
        if(hdfs.isDirectory(mypath)){
            hdfs.delete(mypath,true);
        }

        //创建job对象
        Job job = Job.getInstance(conf, "wordcount");

        //设置运行job的类
        job.setJarByClass(WordCountMapReduce.class);

        //设置mapper 类
        job.setMapperClass(WordcountMapper.class);

        //设置reduce 类
        job.setReducerClass(WordcountReducer.class);

        //设置map输出的key value
        job.setMapOutputKeyClass(Text.class);
        job.setMapOutputValueClass(IntWritable.class);

        //设置reduce 输出的 key value
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(IntWritable.class);

        //设置输入输出的路径
        FileInputFormat.setInputPaths(job, new Path(args[0]));
        FileOutputFormat.setOutputPath(job, new Path(args[1]));

        //提交job
        boolean b = job.waitForCompletion(true);
        if(!b){
            System.out.println("wordcount task fail!");
        }
    }
}