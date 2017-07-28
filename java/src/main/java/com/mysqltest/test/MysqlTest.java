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
	
	static int  count=100000;//总次数
	
	//一定要写rewriteBatchedStatements参数，Mysql批量插入才性能才理想
	static String mySqlUrl="jdbc:mysql://127.0.0.1:3306/test?rewriteBatchedStatements=true";
	static String mySqlUserName="root";  
	static String mySqlPassword="cloudera";  
	
	//batchsize
	// static int[] divPoint={count,10000,1000,100,10,1};
	
    public static void main(String[] args) {  
        String[] tableName={"test1","test2","test3"};
        String inputPath="/mnt/mysqlTest/data.txt";
        ArrayList<tableStruct> values;
        try{
            values=getData(inputPath);
        }catch(IOException e){
            System.out.println("inputPath error");
            values=null;
        }
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
        // insertBatch(values,tableName[2],"ignore",10000);
        // loadAll(tableName[2],"ignore");
        // loadAll(tableName[2],"replace");
        // -------分批load-------------
        // loadAll(tableName[2],"ignore");
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
    public static ArrayList<tableStruct> getData(String inputPath)throws IOException
    {
        ArrayList<tableStruct> values=new ArrayList<tableStruct>();
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
    public static void print(String key,long startTime,long endTime){
    	// System.out.println("每执行"+count+"次sql提交一次事务");
    	System.out.println(key+",用时"+ (endTime-startTime)/1000.0+" s,平均每秒执行"+(count*1000/(endTime-startTime))+"条");
    	System.out.println("----------------------------------");
    }
    /** 
     * mysql非批量插入10万条记录 
     */  
    public static void insertPerline(ArrayList<tableStruct> values,String tableName,String mode){  
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
            print("MySql依次插入10万条记录到"+tableName,a,b);   
        } catch (Exception ex) {  
            ex.printStackTrace();  
        }finally{  
            close(conn);    
        }  
    }  
    
    /** 
     * mysql批量插入10万条记录 
     */  
    public static void insertBatch(ArrayList<tableStruct> values, String tableName,String mode,int point){  
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
                    prest.executeBatch();
                }    
            } 
            // prest.executeBatch();      
            conn.commit();       
            long b=System.currentTimeMillis();  
            print("MySql批量插入10万条记录到"+tableName,a,b);
        } catch (Exception ex) {  
            ex.printStackTrace();  
        }finally{  
            close(conn);    
        }  
    }

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
            print("MySql一次load ("+mode+") 10万条记录到"+tableName,a,b);
        } catch (Exception ex) {  
            ex.printStackTrace();  
        }finally{  
            close(conn);              
        }
    }  
 
}  