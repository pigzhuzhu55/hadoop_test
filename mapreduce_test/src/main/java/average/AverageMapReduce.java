package average;

import java.io.IOException;
import java.util.StringTokenizer;

import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;

public class AverageMapReduce extends Configured implements Tool{

    public static class AverageMapper extends Mapper<Object, Text,Text, IntWritable> {

        protected void map(Object key, Text value, Context context) throws IOException, InterruptedException{
            //得到输入的每一行数据
            String line = value.toString();
            //字符串切割
            StringTokenizer st = new StringTokenizer(line);
            while (st.hasMoreTokens()){
                String strName = st.nextToken(); //学员姓名
                String strScore = st.nextToken();//学员分数

                context.write(new Text(strName),new IntWritable(Integer.parseInt(strScore)));
            }
        }
    }

    public static class AverageReducer extends Reducer<Text,IntWritable,Text,IntWritable>{

        @Override
        protected void reduce(Text key, Iterable<IntWritable> values, Context context) throws IOException, InterruptedException {

            int sum=0; //总成绩
            int num = 0; //总科目数

            for(IntWritable score:values){
                sum+=score.get();
                num++;
            }

            context.write(key,new IntWritable((int)sum/num));
        }
    }

    public int run(String[] args) throws Exception {

        //任务和参数
        Job job = Job.getInstance(getConf(), "Avg") ;
        job.setJarByClass(AverageMapReduce.class);

        /*设置map方法的类*/
        job.setMapperClass(AverageMapper.class);
        job.setReducerClass(AverageReducer.class);

        /*设置输出的key和value的类型*/
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(IntWritable.class);

        /*设置输入输出参数*/
        FileInputFormat.addInputPath(job, new Path(args[0]));
        FileOutputFormat.setOutputPath(job, new Path(args[1]));

        /*提交作业到集群并等待任务完成*/
        boolean isSuccess = job.waitForCompletion(true);

        return isSuccess ? 0 : 1 ;
    }

    public void setConf(org.apache.hadoop.conf.Configuration configuration) {

    }

    public org.apache.hadoop.conf.Configuration getConf() {
        return  new org.apache.hadoop.conf.Configuration();
    }


    public static void main(String[] args)throws Exception{

        //输入输出目录指定
        args = new String[2];
        args[0]="data/average/input/";
        args[1]="data/average/output/";

        //重置输出目录（删除输出目录）
        Path mypath =  new Path(args[1]);
        FileSystem hdfs = mypath.getFileSystem(new org.apache.hadoop.conf.Configuration());
        if(hdfs.isDirectory(mypath)){
            hdfs.delete(mypath,true);
        }

        int res = ToolRunner.run(new AverageMapReduce(),args);
        System.exit(res);
    }
}


