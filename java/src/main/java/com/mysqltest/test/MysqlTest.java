package com.mysqltest.test;
import java.sql.Connection;  
import java.sql.DriverManager;  
import java.sql.PreparedStatement;  
import java.sql.SQLException;  
import java.sql.Statement;
// import java.text.SimpleDateFormat;
import java.sql.Date;
// /**
//    MySql 插入(insert)性能测试
//    Oracle 插入(insert)性能测试

// 	MySql建表语句：
// 	CREATE  TABLE `dev`.`test_insert` (
// 	  `id` INT NOT NULL ,
// 	  `uname` VARCHAR(10) NULL ,
// 	  PRIMARY KEY (`id`) )
// 	ENGINE = InnoDB;
//  */
public class MysqlTest {  
	
	static int  count=40000;//总次数
	
	//一定要写rewriteBatchedStatements参数，Mysql批量插入才性能才理想
	static String mySqlUrl="jdbc:mysql://127.0.0.1:3306/test?rewriteBatchedStatements=true";
	static String mySqlUserName="root";  
	static String mySqlPassword="cloudera";  
	
	// static String oracleurl="jdbc:oracle:thin:@192.168.10.139:1521:orcl";  
	// static String oracleuserName="scott";  
	// static String oraclepassword="tiger"; 
	
	static String sql = "insert into test1 values(?,?,?,?,?)"; 
	
	//每执行几次提交一次
	// static int[] commitPoint={count,10000,1000,100,10,1};
	
    public static void main(String[] args) {  
    	test_mysql(count);
        // for(int point:commitPoint){
     //        test_mysql(point);  
    	// }
    	// for(int point:commitPoint){
     //        test_mysql_batch(point);  
    	// }
    }  
    
    /**
     * 创建连接
     * @return
     */
    public static Connection getConn(String flag){
    	long a=System.currentTimeMillis();
        try {        
        	if("mysql".equals(flag)){
                Class.forName("com.mysql.jdbc.Driver");        
                Connection conn =  DriverManager.getConnection(mySqlUrl, mySqlUserName, mySqlPassword);     
                conn.setAutoCommit(false);  
                return conn;
        	}else{
        		System.out.println();
        		throw new RuntimeException("flag参数不正确,flag="+flag);
        	}
        }catch (Exception ex) {  
            ex.printStackTrace();  
        }finally{  
        	long b=System.currentTimeMillis();  
            System.out.println("创建连接用时"+ (b-a)+" ms"); 
        }
        return null;
    }
    /**
     * 关闭连接
     * @return
     */
    public static void close(Connection conn){
    	 try {  
             if(conn!=null){
            	 conn.close();  
             }
         } catch (SQLException e) {  
             e.printStackTrace();  
         }
    }
    /**
     * 删除旧数据
     * @return
     */
    public static void clear(Connection conn){
    	try{
            Statement st=conn.createStatement();
            boolean bl=st.execute("truncate table test1");
            conn.commit();
            st.close();
            System.out.println("执行清理操作："+(bl==false?"成功":"失败"));
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    }
    /**
     * 打印信息
     * @return
     */
    public static void print(String key,long startTime,long endTime,int point){
    	System.out.println("每执行"+point+"次sql提交一次事务");
    	System.out.println(key+"，用时"+ (endTime-startTime)/1000+" s,平均每秒执行"+(count*1000/(endTime-startTime))+"条");
    	System.out.println("----------------------------------");
    }
    /** 
     * mysql非批量插入10万条记录 
     */  
    public static void test_mysql(int point){  
        Connection conn=getConn("mysql");  
        clear(conn);
        try {        
            PreparedStatement prest = conn.prepareStatement(sql);        
            long a=System.currentTimeMillis();  
            for(int x = 1; x <= count; x++){        
                // Date dd=new Date();
                // SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd");
                // dd=format.format("1993-06-20");
                prest.setString(1,"zhaoxiao");
                prest.setDate(2,new Date(System.currentTimeMillis()));        
                prest.setInt(3,x);
                prest.setInt(4,x);
                prest.setInt(5,x);        
                prest.execute();  
                if(x%point==0){
            	   conn.commit();
                }
            }        
            long b=System.currentTimeMillis();  
            print("MySql非批量插入10万条记录",a,b,point);
        } catch (Exception ex) {  
            ex.printStackTrace();  
        }finally{  
            close(conn);    
        }  
    }  
    
    /** 
     * mysql批量插入10万条记录 
     */  
    public static void test_mysql_batch(int point){  
        Connection conn=getConn("mysql");  
        clear(conn);
        try {        
            PreparedStatement prest = conn.prepareStatement(sql);        
            long a=System.currentTimeMillis();  
            for(int x = 1; x <= count; x++){        
                prest.setInt(1, x);        
                prest.setString(2, "张三");        
                prest.addBatch();    
                if(x%point==0){
                	prest.executeBatch();      
                	conn.commit();
                }
            }        
            long b=System.currentTimeMillis();  
            print("MySql批量插入10万条记录",a,b,point);
        } catch (Exception ex) {  
            ex.printStackTrace();  
        }finally{  
            close(conn);    
        }  
    }  
 
}  