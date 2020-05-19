import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.CellUtil;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Admin;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.ConnectionFactory;
import org.apache.hadoop.hbase.client.Delete;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.util.Bytes;

public class HbaseJavaTest {
	/*封装公用模块*/
	private static Configuration conf = null ;
	private static Connection conn = null ;
	static {
		conf = HBaseConfiguration.create() ;
		//配置hbase的zookeeper
		conf.set("hbase.zookeeper.quorum", "master");
		//conn
		try {
			conn = ConnectionFactory.createConnection(conf) ;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	//添加一条数据及批量
	public static void addOneData() throws Exception{
		//创建表对象
		Table ht = conn.getTable(TableName.valueOf("people")) ;
		
		//创建put对象
		Put put = new Put(Bytes.toBytes("p00001")) ;
		put.addColumn(Bytes.toBytes("cinfo"), Bytes.toBytes("name"), Bytes.toBytes("xiaoshi")) ;
		
//		List<Put> plist = new ArrayList()<Put>(10000) ;
//		for(int i = 0 ; i < 10000 ; i ++){
//			Put put = new Put(Bytes.toBytes("Ande0000"+i)) ;
//			put.addColumn(Bytes.toBytes("cinfo"), Bytes.toBytes("name"), Bytes.toBytes("xiaoshi" + i)) ;
//			plist.add(put) ;
//		}
		
		//添加put到表对象
		ht.put(put);
		//关闭资源
		ht.close(); 
		System.out.println("===========数据插入成功===========");
		
		
	}
	
	//获得表描述方法
	public static void getTableDesc(String tableName) throws Exception{
		Table ht = conn.getTable(TableName.valueOf("people")) ;//HTable ht = new HTable(conf, "people") ;			//获得表对象
		HTableDescriptor td = ht.getTableDescriptor();		//获得表描述对象
		HColumnDescriptor[] hds = td.getColumnFamilies();	//获得列描述对象数组
		for(HColumnDescriptor hd : hds){
			String name = hd.getNameAsString();				//列族名
			int bs = hd.getBlocksize() ;
			int minVers = hd.getMinVersions() ;
			int maxVers = hd.getMaxVersions() ;
			int defVers = HColumnDescriptor.DEFAULT_VERSIONS ;
			System.out.println("name : " + name + 
					" blocksize : " + bs +
					" minVers : " + minVers + 
					" maxVers : " + maxVers + " defVers : " + defVers);
			
		}
		//释放资源
		ht.close(); 
	}
	
	//扫描表的所有数据
	public static void scanTable(String tableName) throws Exception{
		Table ht = conn.getTable(TableName.valueOf("people")) ;
		
		Scan scan = new Scan() ;
		//ResultScanner是客户端获取值的接口
		ResultScanner scanner = ht.getScanner(scan);
		
		//每行的数据就是Result，存储GET获得SCAN操作后获得单行的值
		for(Result res : scanner){
			for(Cell cell : res.listCells()){
				System.out.println("================================================");
				System.out.println("行键：rowkey ===========" + Bytes.toString(res.getRow()));
				System.out.println("列族：columnFam ========" + Bytes.toString(CellUtil.cloneFamily(cell)));
				System.out.println("列：column ============" + Bytes.toString(CellUtil.cloneQualifier(cell)));
				System.out.println("时间戳：timestamp =======" + cell.getTimestamp());
				System.out.println("值：value ==============" + Bytes.toString(CellUtil.cloneValue(cell)));
			}
		}
		//释放资源
		ht.close();
	}
	
	//获得多行的scan数据
	public static void scanForRange() throws Exception{
		Table ht = conn.getTable(TableName.valueOf("people")) ;
		Scan scan = new Scan(Bytes.toBytes("Ande0000500"), Bytes.toBytes("Ande0000600")) ;
		ResultScanner scanner = ht.getScanner(scan);
		for(Result rs : scanner){
			//获得某个列的值
			String res = Bytes.toString(rs.getValue(Bytes.toBytes("cinfo"), Bytes.toBytes("name"))) ;
			System.out.println(res);
		}
		ht.close();
	}
	
	//获得单行的数据
	public static void getForRowKey(String rowkey) throws Exception{
		Table ht = conn.getTable(TableName.valueOf("people")) ;			//获得表对象
		Get get = new Get(Bytes.toBytes(rowkey)) ;
		
		Result result = ht.get(get);
		if( result == null || result.size() == 0){
			System.out.println("没有这个rowkey");
			ht.close();
			return ;
		}
		for(Cell cell : result.listCells()){
			System.out.println("================================================");
			System.out.println("行键：rowkey ===========" + Bytes.toString(result.getRow()));
			System.out.println("列族：columnFam ========" + Bytes.toString(CellUtil.cloneFamily(cell)));
			System.out.println("列：column ============" + Bytes.toString(CellUtil.cloneQualifier(cell)));
			System.out.println("时间戳：timestamp =======" + cell.getTimestamp());
			System.out.println("值：value ==============" + Bytes.toString(CellUtil.cloneValue(cell)));
		}
		ht.close(); 
	}
	
	//删除数据
	public static void deleteRow(String rowkey) throws Exception{
		Table ht = conn.getTable(TableName.valueOf("people")) ;			//获得表对象
		Delete delete = new Delete(Bytes.toBytes(rowkey)) ;
		ht.delete(delete);
		ht.close();
	}
		
	//修改表，添加列族
	public static void alterTableAddCls() throws Exception {
		//创建数据库管理员
		Admin admin = conn.getAdmin() ;
		admin.disableTable(TableName.valueOf("people"));
		HColumnDescriptor hcd = new HColumnDescriptor(Bytes.toBytes("age")) ;
		
		//1:获得表描述对象进行修改
		HTableDescriptor td = admin.getTableDescriptor(TableName.valueOf("people"));
		td.addFamily(hcd);
		
		//通过admin来进行实际的修改
		admin.modifyTable(TableName.valueOf(("people")), td);
		admin.enableTable(TableName.valueOf("people"));
		
		admin.close();
		System.out.println("====添加列族成功====");
	}
	
	//删除该表
	public static void deleteTable() throws Exception{
		Admin admin = conn.getAdmin() ;
		if(admin.tableExists(TableName.valueOf("people"))){
			admin.disableTable(TableName.valueOf("people"));
			admin.deleteTable(TableName.valueOf("people"));
			System.out.println("删除表成功");
		}
		admin.close();
	}
		
	public static void main(String[] args) throws Exception {
		//获得表描述信息
//		getTableDesc("people") ;
		//插入单条数据
//		addOneData() ;
		//扫描数据
//		scanTable("people") ;
		//扫描多行
//		scanForRange() ;
		//获得单行数据
//		getForRowKey("Ande0000500") ;
		//添加列族
//		alterTableAddCls() ;
		
		conn.close();
	}
}
