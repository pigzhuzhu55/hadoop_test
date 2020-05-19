package casewords;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;


public class CaseWordsMapReduce extends Configured implements Tool{

    public int run(String[] args) throws Exception {
        Configuration conf = new Configuration() ;
        //删除已经存在的输出目录
        Path mypath = new Path(args[1]) ;
        FileSystem hdfs = mypath.getFileSystem(conf);
        if(hdfs.isDirectory(mypath)){
            hdfs.delete(mypath, true) ;
        }

        //设置任务信息
        Job job = Job.getInstance(conf, "words mr") ;
        job.setJarByClass(CaseWordsMapReduce.class);

        /*设置map方法的类*/
        job.setMapperClass(CaseWordsMapper.class);

        job.setReducerClass(CaseWordsReducer.class);

        /*设置输出的key和value的类型*/
        job.setOutputKeyClass(Text.class);
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
        args[0]="data/casewords/input/";
        args[1]="data/casewords/output/";

        //重置输出目录（删除输出目录）
        Path mypath =  new Path(args[1]);
        FileSystem hdfs = mypath.getFileSystem(new org.apache.hadoop.conf.Configuration());
        if(hdfs.isDirectory(mypath)){
            hdfs.delete(mypath,true);
        }

        int res = ToolRunner.run(new CaseWordsMapReduce(), args) ;
        System.exit(res);
    }

}
