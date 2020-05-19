package casewords;
import java.io.IOException;
import java.util.Arrays;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

public class CaseWordsMapper extends Mapper<Object, Text, Text, Text>{
    private Text keyText = new Text() ;
    private Text valueText = new Text() ;

    @Override
    protected void map(Object key, Text value, Context context)
            throws IOException, InterruptedException {
        String word = value.toString() ;
        char[] wordChars = word.toCharArray();	//单词转化为字符数组
        Arrays.sort(wordChars); 				//对字符数组进行排序
        String sword = new String(wordChars) ;	//字符数组再转化为字符串
        keyText.set(sword);              		//设置输出key
        valueText.set(word);  					//设置输出得value得值
        context.write(keyText, valueText);		//map输出
    }
}

