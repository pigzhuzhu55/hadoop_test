package casefilter;

import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;
import org.apache.hadoop.fs.Path;

import java.io.IOException;

public class CaseFilterMapReduce extends Configured implements Tool {

    public  static class FilterMapper extends Mapper<Object,Text, NullWritable, Text>{
        protected void map(Object key, Text value, Context context) throws IOException, InterruptedException{
            //得到输入的每一行数据, 字符串切割
            String[] strArr = value.toString().split(" ");

            String str = String.format("%s %s %s %s",strArr[0],strArr[1],strArr[2],strArr[6]);
            context.write(NullWritable.get(),new Text(str));
        }
    }

    public int run(String[] args) throws Exception {
        Job job = Job.getInstance(new org.apache.hadoop.conf.Configuration(), "mrfilter") ;
        job.setJarByClass(CaseFilterMapReduce.class);

        /*设置map方法的类*/
        job.setMapperClass(FilterMapper.class);

        /*设置输出的key和value的类型*/
        job.setOutputKeyClass(NullWritable.class);
        job.setOutputValueClass(Text.class);

        /*设置输入输出参数*/
        FileInputFormat.addInputPath(job, new Path(args[0]));
        FileOutputFormat.setOutputPath(job, new Path(args[1]));

        /*提交作业到集群并等待任务完成*/
        boolean isSuccess = job.waitForCompletion(true);

        return isSuccess ? 0 : 1 ;
    }

    public static void main(String[] args) throws Exception {

        //输入输出目录指定
        args = new String[2];
        args[0]="data/casefilter/input/";
        args[1]="data/casefilter/output/";

        //重置输出目录（删除输出目录）
        Path mypath =  new Path(args[1]);
        FileSystem hdfs = mypath.getFileSystem(new org.apache.hadoop.conf.Configuration());
        if(hdfs.isDirectory(mypath)){
            hdfs.delete(mypath,true);
        }

        int res = ToolRunner.run(new CaseFilterMapReduce(), args) ;
        System.exit(res);
    }
}
