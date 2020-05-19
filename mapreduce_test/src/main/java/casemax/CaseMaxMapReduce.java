package casemax;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.util.ToolRunner;

public class CaseMaxMapReduce {
    public static void main(String[] args)throws Exception{
        //创建配置对象
        Configuration conf = new Configuration();

        //输入输出目录指定
        args = new String[2];
        args[0]="data/casemax/input/";
        args[1]="data/casemax/output/";

        //重置输出目录（删除输出目录）
        Path mypath =  new Path(args[1]);
        FileSystem hdfs = mypath.getFileSystem(conf);
        if(hdfs.isDirectory(mypath)){
            hdfs.delete(mypath,true);
        }

        int rs = ToolRunner.run(new CaseMaxMapper(),args);
        System.exit(rs);
    }
}
