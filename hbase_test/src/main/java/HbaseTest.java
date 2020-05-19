import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Admin;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.ConnectionFactory;

public class HbaseTest {

	public static void main(String[] args) throws Exception {
		/*
		 * 我们一般通过来获取Configuration，设置一些参数
		 * 比如zk的地址，端口等
		 * */
		Configuration conf = HBaseConfiguration.create() ;
		conf.set("hbase.zookeeper.quorum", "master");
		conf.set("hbase.master.hostname", "master");

		
		/*Connection用来获取和hbase的链接*/
		Connection conn = ConnectionFactory.createConnection(conf) ;
		
		/*
		 * Admin这个类主要用来创建表，删除表，启用禁用表等操作的接口类
		 * 过期的类叫HBaseAdmin
		 * */
		Admin admin = conn.getAdmin() ;
		
		/*
		 * 
		 * HTableDescriptor 表描述信息的接口类
		 * TableName 		描述表名称的接口类，把字符串（表名）变成hbase所认识的
		 * HColumnDescriptor 列族的描述信息类，比如版本，压缩方式等等
		 * Put				添加数据的时候需要用到，可以批量添加也可以单条添加
		 * 					若是批量添加，需要创建一个list，将put对象放入
		 * */
		HTableDescriptor table = new HTableDescriptor(TableName.valueOf("people")) ;
		HColumnDescriptor cf = new HColumnDescriptor("cinfo") ;
		cf.setMaxVersions(3) ;
		//添加列族
		table.addFamily(cf) ;
		
		//创建表
		if(!admin.tableExists(TableName.valueOf("people"))){
			admin.createTable(table);
		}else{
			admin.disableTable(TableName.valueOf("people"));
			admin.deleteTable(TableName.valueOf("people"));
			System.out.println("该表已经存在，删除成功！");
		}
		//释放资源
		admin.close();
		System.out.println("===表创建成功===");
	}

}










