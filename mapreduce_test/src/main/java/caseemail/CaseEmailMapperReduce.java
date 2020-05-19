package caseemail;

import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.MultipleOutputs;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;

public class CaseEmailMapperReduce extends Configured implements Tool{

    public static class EmailMapper extends Mapper<LongWritable, Text, Text, IntWritable>{
        private final static IntWritable one = new IntWritable(1) ;
        @Override
        protected void map(LongWritable key, Text value,Context context)
                throws IOException, InterruptedException {
            context.write(value, one);
        }
    }

    public static class EmailReducer extends Reducer<Text, IntWritable, Text, IntWritable>{
        private IntWritable result = new IntWritable() ;
        //输出到多个文件或多个文件夹，使用Multipleoutputs
        private MultipleOutputs<Text, IntWritable> mout ;

        @Override
        protected void setup(Context context)
                throws IOException, InterruptedException {
            mout = new MultipleOutputs<Text, IntWritable>(context) ;
        }

        @Override
        protected void reduce(Text key, Iterable<IntWritable> values,
                              Context context) throws IOException, InterruptedException {
            int begin = key.toString().indexOf("@") ;
            int end = key.toString().indexOf(".") ;
            if(begin >= end){
                return ;
            }

            //获取邮箱类别，比如qq，163等
            String name = key.toString().substring(begin + 1, end);
            int sum = 0 ;
            for (IntWritable value : values) {
                sum += value.get() ;
            }
            result.set(sum);
            //baseoutputpath-r-nnnnn
            mout.write(key, result, name);
        }

        @Override
        protected void cleanup(Context context)
                throws IOException, InterruptedException {
            mout.close();
        }
    }

    public int run(String[] args) throws Exception {
        Configuration conf = new Configuration() ;
        //删除已经存在的输出目录
        Path mypath = new Path(args[1]) ;
        FileSystem hdfs = mypath.getFileSystem(conf);
        if(hdfs.isDirectory(mypath)){
            hdfs.delete(mypath, true) ;
        }

        Job job = Job.getInstance(conf, "emailcount") ;
        job.setJarByClass(CaseEmailMapperReduce.class);

        FileInputFormat.addInputPath(job, new Path(args[0]));
        FileOutputFormat.setOutputPath(job, new Path(args[1]));

        job.setMapperClass(EmailMapper.class);
        job.setReducerClass(EmailReducer.class);

        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(IntWritable.class);
        job.waitForCompletion(true) ;
        return 0;
    }

    public static void main(String[] args) throws Exception {
        //输入输出目录指定
        args = new String[2];
        args[0]="data/caseemail/input/";
        args[1]="data/caseemail/output/";

        //重置输出目录（删除输出目录）
        Path mypath =  new Path(args[1]);
        FileSystem hdfs = mypath.getFileSystem(new org.apache.hadoop.conf.Configuration());
        if(hdfs.isDirectory(mypath)){
            hdfs.delete(mypath,true);
        }

        int res = ToolRunner.run(new CaseEmailMapperReduce(), args) ;
        System.exit(res);
    }
}
