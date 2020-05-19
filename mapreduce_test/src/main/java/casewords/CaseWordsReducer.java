package casewords;

import java.io.IOException;
import java.util.StringTokenizer;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

public class CaseWordsReducer extends Reducer<Text, Text, Text, Text>{
    private Text outputKey = new Text() ;	//输出key
    private Text outputValue = new Text() ;	//输出的value

    @Override
    protected void reduce(Text key, Iterable<Text> values, Context context)
            throws IOException, InterruptedException {
        String output ="" ;
        //对相同字母组成的单词，使用~符号进行拼接
        for (Text word : values) {
            if(!output.equals("")){
                output = output + "~" ;
            }
            output = output + word.toString() ;
        }
        //输出有两个单词或以上的结果
        StringTokenizer outputTokenize = new StringTokenizer(output, "~") ;
        if(outputTokenize.countTokens() >= 2){
            output = output.replaceAll("~", ",") ;
            outputKey.set(key.toString()); 			//设置key的值
            outputValue.set(output);				//设置value的值
            context.write(outputKey, outputValue);	//输出
        }
    }


}
