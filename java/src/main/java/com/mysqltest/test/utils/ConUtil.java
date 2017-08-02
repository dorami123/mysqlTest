package com.mysqltest.test.utils;
import java.sql.Connection; 
import java.sql.DriverManager;
import java.sql.Statement;
import java.sql.SQLException; 

public class ConUtil{
	
	static String mySqlUrl;
	static String mySqlUserName;  
	static String mySqlPassword;
	
	public ConUtil(String mySqlUrl,String mySqlUserName,String mySqlPassword){
		this.mySqlUrl=mySqlUrl;
		this.mySqlUserName=mySqlUserName;
		this.mySqlPassword=mySqlPassword;
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
}