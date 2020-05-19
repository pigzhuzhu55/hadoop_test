package dataformat;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import org.apache.hadoop.io.WritableComparable;

public class ScoreWritable implements WritableComparable<Object>{

    //在自定义的数据类型中，建议使用java原生的数据类型
    private float chinese ;
    private float math ;
    private float english ;
    private float physics ;
    private float chemistry ;

    //在自定义的数据类型中，必须要有一个无参的构造方法
    public ScoreWritable(){}

    public ScoreWritable(float chinese, float math, float english, float physics, float chemistry) {
        this.chinese = chinese;
        this.math = math;
        this.english = english;
        this.physics = physics;
        this.chemistry = chemistry;
    }

    public void set(float chinese, float math, float english, float physics, float chemistry){
        this.chinese = chinese;
        this.math = math;
        this.english = english;
        this.physics = physics;
        this.chemistry = chemistry;
    }

    public float getChinese() {
        return chinese;
    }

    public float getMath() {
        return math;
    }

    public float getEnglish() {
        return english;
    }

    public float getPhysics() {
        return physics;
    }

    public float getChemistry() {
        return chemistry;
    }

    //是在写入数据的时候调用，进行序列化
    public void write(DataOutput out) throws IOException {
        out.writeFloat(chinese);
        out.writeFloat(math);
        out.writeFloat(english);
        out.writeFloat(physics);
        out.writeFloat(chemistry);
    }

    //该方法是在取出数据时调用，反序列化，以便生成对象
    public void readFields(DataInput in) throws IOException {
        chinese = in.readFloat() ;
        math = in.readFloat() ;
        english = in.readFloat() ;
        physics = in.readFloat() ;
        chemistry = in.readFloat() ;
    }

    public int compareTo(Object o) {
        // TODO Auto-generated method stub
        return 0;
    }
}
