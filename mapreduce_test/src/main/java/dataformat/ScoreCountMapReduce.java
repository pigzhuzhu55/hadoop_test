package dataformat;

import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;

public class ScoreCountMapReduce extends Configured implements Tool{

    //map和reduce
    public static class ScoreMapper extends Mapper<Text, ScoreWritable, Text, ScoreWritable>{
        @Override
        protected void map(Text key, ScoreWritable value,
                           Context context)
                throws IOException, InterruptedException {
            context.write(key, value);
        }
    }

    public static class ScoreReducer extends Reducer<Text, ScoreWritable, Text, Text>{
        private Text text = new Text() ;
        @Override
        protected void reduce(Text key, Iterable<ScoreWritable> value,
                              Context context) throws IOException, InterruptedException {
            float totalScore = 0.0f ;
            float avgScore = 0.0f ;
            for (ScoreWritable sw : value) {
                totalScore = sw.getChinese() + sw.getEnglish() + sw.getMath() + sw.getPhysics() + sw.getChemistry() ;
                avgScore = totalScore/5 ;
            }
            text.set(totalScore + "\t" + avgScore);
            context.write(key, text);
        }
    }

    public int run(String[] args) throws Exception {

        Job job = Job.getInstance(new Configuration() , "scorecount") ;
        job.setJarByClass(ScoreCountMapReduce.class);

        FileInputFormat.addInputPath(job, new Path(args[0]));
        FileOutputFormat.setOutputPath(job, new Path(args[1]));

        job.setMapperClass(ScoreMapper.class);
        job.setReducerClass(ScoreReducer.class);

        //如果是自定义的类型，需要进行设置
        job.setMapOutputKeyClass(Text.class);
        job.setMapOutputValueClass(ScoreWritable.class);

        //设置自定义的输入格式
        job.setInputFormatClass(ScoreInputFormat.class);

        job.waitForCompletion(true) ;
        return 0;
    }

    public static void main(String[] args) throws Exception {
        //输入输出目录指定
        args = new String[2];
        args[0]="data/dataformat/input/";
        args[1]="data/dataformat/output/";

        //重置输出目录（删除输出目录）
        Path mypath =  new Path(args[1]);
        FileSystem hdfs = mypath.getFileSystem(new org.apache.hadoop.conf.Configuration());
        if(hdfs.isDirectory(mypath)){
            hdfs.delete(mypath,true);
        }

        int res = ToolRunner.run(new ScoreCountMapReduce(), args) ;
        System.exit(res);
    }
}
