package pl.cba.knest.TabErr;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
public class Database{
    private String hostname;
    private String username;
    private String portnum;
    private String dbname;
    private String password;
    protected Connection conn;

    public Database(String host, String user, String port, String pass, String db){
        hostname = host;
        username = user;
        portnum = port;
        dbname = db;
        password = pass;
    }
    protected boolean initialize(){
    	try{
    		Class.forName("com.mysql.jdbc.Driver");
    	}catch(ClassNotFoundException e){
    		return false;
    	}
    	return true;
    }
    public boolean open(){
        if(initialize()){
            String url = "";
            try{
                url = "jdbc:mysql://"+hostname+":"+portnum+"/"+dbname;
                conn = DriverManager.getConnection(url, username, password);
                
                return true;
            }
            catch(SQLException e){	
            	TabErr.log(e.getMessage());
            }
        }
        return false;
    }
    public void close(){
        try{
            if(conn != null)
            	conn.close();
        }catch(Exception e){
        	//TabErr.log("Exception on closing database");
        }
    }
    public ResultSet query(String query){
        Statement statement = null;
        ResultSet result = null;
        try{
            if(conn == null || conn.isClosed())
                open();
            	
            statement = conn.createStatement();
            result = statement.executeQuery("SELECT CURTIME()");
            if(query.startsWith("SELECT"))
                result = statement.executeQuery(query);
            else
                statement.executeUpdate(query);
            return result;
        }catch(SQLException e){
        	TabErr.log(e.getMessage());
        	
        }
        return result;
    }
}
