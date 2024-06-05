package datastreamcdc.sink;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.sink.RichSinkFunction;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

public class AccountAlertCdc2MysqlSink extends RichSinkFunction<String>{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Connection connection = null;
	private Statement st = null;
	
	private String username;
	private String password;
	private String jdbcUrl;
	private String table;
	private List<String> colum;
	
	/**
	 * op的取值 根据Debezium官网
	 * Mandatory string that describes the type of operation that caused the connector to 
	 * generate the event. In this example, c indicates that the operation created a row. 
	 * Valid values are:
	 * c = create
	 * u = update
	 * d = delete
	 * r = read (applies to only snapshots)
	 */
	String op;
	
	private static final String OP_CREATE = "c";
	private static final String OP_UPDATE = "u";
	private static final String OP_DELETE = "d";
	
	public AccountAlertCdc2MysqlSink(String username, String password, String jdbcUrl,String table,List<String> column) {
		this.username = username;
		this.password = password;
		this.jdbcUrl = jdbcUrl;
		this.table = table;
		this.colum = column;
	}

	// 只执行一次，用于创建数据库连接
	@Override
	public void open(Configuration parameters) throws Exception {
		System.out.println("----------------sink-close--------------");
		Class.forName("com.mysql.jdbc.Driver");
		connection = DriverManager.getConnection(jdbcUrl, username, password);
	}
	
	//执行数据入库操作
	@Override
	public void invoke(String accountTransationStr, Context context) throws Exception {
		System.out.println("----------------sink-invoke--------------");
		
		JSONObject jsonObject = (JSONObject) JSON.parse(accountTransationStr);
		
		op = jsonObject.getString("op");
		switch (op) {
			case OP_CREATE:
				insertRecord(jsonObject);
				break;
			case OP_UPDATE:
				updateRecord(jsonObject);
				break;
			case OP_DELETE:
				deleteRecord(jsonObject);
				break;
			default:
				break;
		}
	}

	private void deleteRecord(JSONObject jsonObject) throws Exception {
		JSONObject beforeJsonObject = (JSONObject) jsonObject.get("before");
		StringBuilder sb = new StringBuilder();
		sb.append("delete from ");
		sb.append(this.table);
		sb.append(" where ");
		for (int i = 0; i < this.colum.size(); i++) {
			sb.append(this.colum.get(i));
			sb.append(" = ");
			sb.append(beforeJsonObject.get(this.colum.get(i)));
			if (i != this.colum.size() - 1) {	
				sb.append(" and ");
			}
		}
		sb.append(";");
		String sql = sb.toString();
		System.out.println("delete-sql >>>>>>>>  " + sql);
		st = connection.createStatement();
		st.execute(sql);
	}

	private void updateRecord(JSONObject jsonObject) throws Exception {
		JSONObject beforeJsonObject = (JSONObject) jsonObject.get("before");
		JSONObject afterJsonObject = (JSONObject) jsonObject.get("after");
		
		StringBuilder sb = new StringBuilder();
		sb.append("update ");
		sb.append(this.table);
		sb.append(" set ");
		for (int i = 0; i < this.colum.size(); i++) {
			sb.append(this.colum.get(i));
			sb.append(" = ");
			sb.append(afterJsonObject.get(this.colum.get(i)));
			if (i != this.colum.size() - 1) {	
				sb.append(",");
			}
		}
		sb.append(" where ");
		for (int i = 0; i < this.colum.size(); i++) {
			sb.append(this.colum.get(i));
			sb.append(" = ");
			sb.append(beforeJsonObject.get(this.colum.get(i)));
			if (i != this.colum.size() - 1) {	
				sb.append(" and ");
			}
		}
		sb.append(";");
		String sql = sb.toString();
		System.out.println("update-sql >>>>>>>>  " + sql);
		st = connection.createStatement();
		st.execute(sql);
	}

	private void insertRecord(JSONObject jsonObject) throws Exception {
		JSONObject afterJsonObject = (JSONObject) jsonObject.get("after");
		// after 里面获取的就是所有的值
		StringBuilder sb = new StringBuilder();
		sb.append("INSERT INTO ");
		sb.append(this.table);
		sb.append("(");
		sb.append(String.join(",", this.colum));
		sb.append(") values (");
		for (int i = 0; i < this.colum.size(); i++) {
			sb.append(afterJsonObject.get(this.colum.get(i)));
			if (i != this.colum.size() - 1) {	
				sb.append(",");
			}
		}
		sb.append(");");
		String sql = sb.toString();
		st = connection.createStatement();
		st.execute(sql);
	}

	//只执行一次，用于关闭数据库连接
	@Override
	public void close() throws Exception {
		try {
			if(connection !=null) {
				connection.close();
				connection = null ;
			}
			
			if(st!=null) {
				st.close();
				st = null ;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
