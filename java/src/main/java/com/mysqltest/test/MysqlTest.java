package com.mysqltest.test;
import java.sql.Connection;  
import java.sql.DriverManager;  
import java.sql.PreparedStatement;  
import java.sql.SQLException;  
import java.sql.Statement;
import java.sql.Date;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;

public class MysqlTest {  
	
	static int  count=10000000;//总次数
	//一定要写rewriteBatchedStatements参数，Mysql批量插入才性能才理想
	static String mySqlUrl="jdbc:mysql://127.0.0.1:3306/test?rewriteBatchedStatements=true";
	static String mySqlUserName="root";  
	static String mySqlPassword="cloudera";  
	
	//batchsize
	// static int[] divPoint={count,10000,1000,100,10,1};
	
    public static void main(String[] args) {  
        String[] tableName={"test1","test2","test3"};
        String inputPath="/mnt/mysqlTest/data.txt";
        // --------------一次性读完表-----------------------
        // List<tableStruct> values;
        // try{
        //     values=getData(inputPath,10000000);
        // }catch(IOException e){
        //     System.out.println("inputPath error");
        //     values=null;
        // }
        // --------load 和分批insert效果比较-----
        // for (String tab:tableName){
        //     // insertPerline(values,tab,"ignore");
        //     insertBatch(values,tab,"ignore",10000);
        //     // insertBatch(values,tab,"replace",10000);
        //     loadAll(tab,"ignore");
        //     loadAll(tab,"replace");
        // }

        // --------insert不同大小分批的效果--------
        // insertBatch(values,tab,"ignore",)
        // for (String tab:tableName){
        //     for(int point:divPoint){
        //         insertBatch(values,tab,"ignore",point);
        //     }
        // }

        // -------java和python的比较-------------
        // insertBatch(values,tableName[2],"ignore",100000);
        // loadAll(tableName[2],"ignore");
        // loadAll(tableName[2],"replace");
        // -------分批load-------------
        // loadAll(tableName[2],"ignore");
        // -------java connection-------------

        // mysql批量插入记录,div为划分的个数，每次重新建立连接
        try{
            insertBatchReConn(inputPath,tableName[2],"ignore",100);
        }catch(IOException e){
            System.out.println("inputPath error");
        }
    }  

    
    // 封装行数据
    private static class tableStruct{                 //消息头
        public String  name;          
        public String  birth;   
        public int id;        
        public int height;        
        public int weight;       
    }; 

    // 读取文件
    public static List<tableStruct> getData(String inputPath,int count)throws IOException
    {
        List<tableStruct> values=new ArrayList<tableStruct>();
        FileReader reader = new FileReader(inputPath);
        BufferedReader br = new BufferedReader(reader);          
        for (int i=0;i<count;i++){
            tableStruct value=new tableStruct();
            String[] str=new String[5];
            str=br.readLine().split("\\,");
            value.name=str[0].substring(1,9);
            value.birth=str[1].substring(1,11);
            value.id=Integer.parseInt(str[2]);
            value.height=Integer.parseInt(str[3]);
            value.weight=Integer.parseInt(str[4]);      
            values.add(value);
        }
        return values;
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
    public static void clear(Connection conn, String tableName){
    	try{
            Statement st=conn.createStatement();
            boolean bl=st.execute("truncate table "+tableName);
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
    public static void print(String key,long startTime,long endTime,int batchsize){
    	// System.out.println("每执行"+count+"次sql提交一次事务");
        double speed=batchsize*1000.0/(endTime-startTime);
    	System.out.println(key+",用时"+ (endTime-startTime)/1000.0+" s,平均每秒执行"+speed+"条");
    	System.out.println("----------------------------------");
    }
    /** 
     * mysql非批量插入记录 
     */  
    public static void insertPerline(List<tableStruct> values,String tableName,String mode){  
        String sql="insert "+mode+" into "+ tableName+ " values(?,?,?,?,?)";
        Connection conn=getConn("mysql");  
        clear(conn,tableName);
        try {        
            PreparedStatement prest = conn.prepareStatement(sql);        
            long a=System.currentTimeMillis();  
            for(int i=0;i<count;i++){
                prest.setString(1,values.get(i).name);
                prest.setString(2,values.get(i).birth);        
                prest.setInt(3,values.get(i).id);
                prest.setInt(4,values.get(i).weight);
                prest.setInt(5,values.get(i).height);        
                prest.execute();  
                // if(x%point==0){
            	   // conn.commit();
                // }
            }
            conn.commit();        
            long b=System.currentTimeMillis();  
            print("MySql依次插入"+count+"条记录到"+tableName,a,b,count);   
        } catch (Exception ex) {  
            ex.printStackTrace();  
        }finally{  
            close(conn);    
        }  
    }  
    
    /** 
     * mysql批量插入记录,point为batchsize 
     */  
    public static void insertBatch(List<tableStruct> values, String tableName,String mode,int point){  
        String sql="insert "+mode+" into "+ tableName+ " values(?,?,?,?,?)";
        Connection conn=getConn("mysql");  
        clear(conn,tableName);
        try {        
            PreparedStatement prest = conn.prepareStatement(sql);        
            long a=System.currentTimeMillis();  
            for(int i=0;i<count;i++){
                prest.setString(1,values.get(i).name);
                prest.setString(2,values.get(i).birth);        
                prest.setInt(3,values.get(i).id);
                prest.setInt(4,values.get(i).weight);
                prest.setInt(5,values.get(i).height);        
                prest.addBatch();
                if((i+1)%point==0){
                    long a1=System.currentTimeMillis();
                    prest.executeBatch();
                    long b1=System.currentTimeMillis();
                    double c=point*1000.0/(b1-a1);
                    System.out.println("执行第"+(i+1)/point+"批，每秒执行"+c+"条");
                }    
            } 
            // prest.executeBatch();      
            conn.commit();       
            long b=System.currentTimeMillis();  
            print("MySql批量插入"+count+"条记录到"+tableName,a,b,count);
        } catch (Exception ex) {  
            ex.printStackTrace();  
        }finally{  
            close(conn);    
        }  
    }

    /** 
     * mysql批量插入记录,div为划分的个数，每次重新建立连接
     */  
    public static void insertBatchReConn(String inputPath, String tableName,String mode,int div)throws IOException{
        Connection conn=getConn("mysql");  
        clear(conn,tableName);
        close(conn);
        int batchsize=count/div;

        FileReader reader = new FileReader(inputPath);
        BufferedReader br = new BufferedReader(reader);  

        long a=System.currentTimeMillis();
        for(int i=0;i<div;i++){
            List<tableStruct> values=new ArrayList<tableStruct>();
            for (int j=0;j<batchsize;j++){
                tableStruct value=new tableStruct();
                String[] str=new String[5];
                str=br.readLine().split("\\,");
                value.name=str[0].substring(1,9);
                value.birth=str[1].substring(1,11);
                value.id=Integer.parseInt(str[2]);
                value.height=Integer.parseInt(str[3]);
                value.weight=Integer.parseInt(str[4]);      
                values.add(value);
            }
            System.out.println("执行第"+(i+1)+"批");
            insertOneBatch(values,tableName,mode,batchsize);
        }
        long b=System.currentTimeMillis(); 
        print("MySql分"+div+"次批量插入"+count+"条记录到"+tableName,a,b,count);
    }

    /** 
     * 批量提交一次
     */ 
    public static void insertOneBatch(List<tableStruct> values, String tableName,String mode,int batchsize){  
        String sql="insert "+mode+" into "+ tableName+ " values(?,?,?,?,?)";
        Connection conn=getConn("mysql");  
        try {        
            PreparedStatement prest = conn.prepareStatement(sql);        
            long a=System.currentTimeMillis();  
            for(int i=0;i<batchsize;i++){
                prest.setString(1,values.get(i).name);
                prest.setString(2,values.get(i).birth);        
                prest.setInt(3,values.get(i).id);
                prest.setInt(4,values.get(i).weight);
                prest.setInt(5,values.get(i).height);        
                prest.addBatch();
            }
            prest.executeBatch();      
            conn.commit();       
            long b=System.currentTimeMillis();  
            print("MySql批量插入"+batchsize+"条记录到"+tableName,a,b,batchsize);
        } catch (Exception ex) {  
            ex.printStackTrace();  
        }finally{  
            close(conn);    
        }  
    }
    /** 
     * load文件块
     */ 
    public static void loadAll(String tableName,String mode){
        String loadSql="load data infile '/mnt/mysqlTest/data.txt' "+mode+ " into table "+tableName+ 
         " FIELDS TERMINATED BY ',' OPTIONALLY ENCLOSED BY '\"' LINES TERMINATED BY '\n'";
        Connection conn=getConn("mysql");  
        clear(conn,tableName);
        try{
            PreparedStatement prest = conn.prepareStatement(loadSql);        
            long a=System.currentTimeMillis(); 
            prest.execute();
            conn.commit();       
            long b=System.currentTimeMillis();  
            print("MySql一次load ("+mode+")"+count+"条记录到"+tableName,a,b,count);
        } catch (Exception ex) {  
            ex.printStackTrace();  
        }finally{  
            close(conn);              
        }
    }  
 
}  