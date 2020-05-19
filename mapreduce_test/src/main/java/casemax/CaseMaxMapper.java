package casemax;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.Tool;

import java.io.IOException;
import java.util.StringTokenizer;

public class CaseMaxMapper extends Mapper<Object, Text,LongWritable, NullWritable> implements Tool {

    long max= Long.MIN_VALUE;

    @Override
    protected void map(Object key,Text value,Context context)throws IOException, InterruptedException{
        //切割字符串
        StringTokenizer st = new StringTokenizer(value.toString());
        while (st.hasMoreTokens()){
            String n1 = st.nextToken();
            String n2 = st.nextToken();

            long num1 = Long.parseLong(n1);
            long num2 = Long.parseLong(n2);

            max = num1>max? num1:max;
            max = num2>max? num2:max;
        }
    }

    @Override
    protected void cleanup(Context context) throws IOException, InterruptedException {

        context.write(new LongWritable(max),NullWritable.get());
    }

    public int run(String[] args) throws Exception {

        //设置任务和主类
        Job job = Job.getInstance(getConf(), "casemax");
        job.setJarByClass(CaseMaxMapper.class);

        //设置map方法的类
        job.setMapperClass(CaseMaxMapper.class);

        //设置输出的key 和 value的类型
        job.setOutputKeyClass(LongWritable.class);
        job.setOutputValueClass(NullWritable.class);

        //设置输入输出参数
        //设置输入输出的路径
        FileInputFormat.setInputPaths(job, new Path(args[0]));
        FileOutputFormat.setOutputPath(job, new Path(args[1]));

        //提交job
        boolean isSuccess = job.waitForCompletion(true);
        return isSuccess ? 0 : 1;
    }

    public void setConf(Configuration configuration) {

    }

    public Configuration getConf() {
        return  new Configuration();
    }
}
