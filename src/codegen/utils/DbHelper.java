package codegen.utils;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import codegen.utils.bean.DataBaseColumn;
import codegen.utils.bean.DataBaseTable;

/**数据库操作类，主要是读取字段类型等
 * @author Danny
 *
 */
public class DbHelper {

	private  String DB_URL;
	private  String DB_USERNAME;
	private  String DB_PASSWORD;
	private  String DB_DRIVER = "com.mysql.jdbc.Driver";
	public  String getDB_URL() {
		return DB_URL;
	}
	public  void setDB_URL(String dB_URL) {
		DB_URL = dB_URL;
	}
	public  String getDB_USERNAME() {
		return DB_USERNAME;
	}
	public  void setDB_USERNAME(String dB_USERNAME) {
		DB_USERNAME = dB_USERNAME;
	}
	public  String getDB_PASSWORD() {
		return DB_PASSWORD;
	}
	public  void setDB_PASSWORD(String dB_PASSWORD) {
		DB_PASSWORD = dB_PASSWORD;
	}
	public  String getDB_DRIVER() {
		return DB_DRIVER;
	}
	public  void setDB_DRIVER(String dB_DRIVER) {
		DB_DRIVER = dB_DRIVER;
	}
	
	{
		try {
			Class.forName(DB_DRIVER);

		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	public Connection getConnection() throws SQLException{
		Connection con = null;
		con = DriverManager.getConnection(DB_URL,DB_USERNAME, DB_PASSWORD);

		return con;
	}
	
	public void closeConnection(Connection con) {

		try {
			con.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 获取数据库中表的信息
	 * @throws SQLException 
	 */
	public List<DataBaseTable> getTables() throws SQLException{
		List<DataBaseTable> list = new ArrayList<>();
		
		Connection con = getConnection();
		DatabaseMetaData databaseMetaData = con.getMetaData();
		 
        //获取所有表
        ResultSet rs = databaseMetaData.getTables(con.getCatalog(), "%", "%", new String[]{"TABLE"});
        
        while(rs.next()){
        	DataBaseTable table = new DataBaseTable();
        	table.setTableComment(rs.getString("REMARKS"));
        	table.setTableName(rs.getString("TABLE_NAME"));
        	list.add(table);
        }
        
        closeConnection(con);
        return list;
	}
	
	/**获取列信息
	 * @param tableName
	 * @return
	 * @throws Exception
	 */
	public List<DataBaseColumn> getColumns(String tableName) throws Exception{
		List<DataBaseColumn> list = new ArrayList<>();
		Connection con = getConnection();
		DatabaseMetaData databaseMetaData = con.getMetaData();
		
		ResultSet rs = databaseMetaData.getColumns(null, "%", tableName, "%");
		
		while(rs.next()){
			DataBaseColumn column = new DataBaseColumn();
			column.setColumnComment(rs.getString("REMARKS"));
			column.setColumnName(rs.getString("COLUMN_NAME"));
			column.setSqlType(rs.getInt("DATA_TYPE"));
			column.setSqlTypeName(rs.getString("TYPE_NAME"));
			column.setJavaType(getJavaType(column.getSqlType()));
			list.add(column);
		}
		
		closeConnection(con);
		return list;
	}
	
	/**获取java类型
	 * @param dataType
	 * @return
	 */
	public String getJavaType(int dataType){
		String result = null;
		switch(dataType){
			case 4:case 5:case -6:result = "Integer";break;
			case 1:case -1:case 12:result = "String";break;
			case 8:result = "Double";break;
			case 3:case -5:result = "java.math.BigInteger";break;
			case 6:result = "Float";break;
			case 91:case 92:case 93:result="java.util.Date";break;
			default: result = "String";
		}
		return result;
	}
}
