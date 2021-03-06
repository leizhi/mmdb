package com.mooo.mycoz.db;

import com.mooo.mycoz.common.JDBCUtil;
import com.mooo.mycoz.common.StringUtils;
import com.mooo.mycoz.db.conf.DbConf;
import com.mooo.mycoz.db.pool.DbConnectionManager;
import com.mooo.mycoz.db.sql.AbstractSQL;
import com.mooo.mycoz.db.sql.ProcessSQL;
import com.mooo.mycoz.db.sql.SQLFactory;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DBObject<T> extends DBExecute implements DbProcess{

	private static Log log = LogFactory.getLog(DBObject.class);

	/**
	 * database operation method
	 */

	private ProcessSQL processSQL=null;

	private String prefix=null;

	public DBObject(){
		processSQL=SQLFactory.getInstance();
		
		prefix = DbConf.getInstance().getDbHumpInterval();
		if(prefix !=null && prefix.equals("case")){
			prefix = null;
		}
	}

	public void setField(String fieldName, Object fieldValue, int fieldType,
						 boolean isPrimaryKey) {
		processSQL.setField(fieldName, fieldValue, fieldType, isPrimaryKey);
	}

	public void setLike(String fieldName, Object fieldValue) {
		processSQL.setLike(fieldName, fieldValue);
	}

	public void setLike2(String fieldName, Object fieldValue) {
		processSQL.setLike2(fieldName, fieldValue);
	}

	public void setGreater(String fieldName, Object fieldValue) {
		processSQL.setGreater(fieldName, fieldValue);
	}

	public void setGreaterEqual(String fieldName, Object fieldValue) {
		processSQL.setGreaterEqual(fieldName, fieldValue);
	}

	public void setLess(String fieldName, Object fieldValue) {
		processSQL.setLess(fieldName, fieldValue);
	}

	public void setLessEqual(String fieldName, Object fieldValue) {
		processSQL.setLessEqual(fieldName, fieldValue);
	}

	public void setNotEqual(String fieldName, Object fieldValue) {
		processSQL.setNotEqual(fieldName, fieldValue);
	}

	public void setWhereIn(String fieldName, Object fieldValue) {
		processSQL.setWhereIn(fieldName, fieldValue);
	}

	public void setRecord(int offsetRecord, int maxRecords) {
		processSQL.setRecord(offsetRecord, maxRecords);
	}

	public void addGroupBy(String fieldName) {
		processSQL.addGroupBy(fieldName);
	}

	public void addOrderBy(String fieldName) {
		processSQL.addOrderBy(fieldName);
	}

	/**
	 * add
	 */ 
	public void add(Connection connection) throws SQLException {
		String addSQL = processSQL.addSQL(this);
		execute(connection,addSQL);
	}
	
	public void add() throws SQLException {
		add(null);
	}

	/**
	 * update
	 */ 
	public void update(Connection connection) throws SQLException {
		String updateSQL = processSQL.updateSQL(this);
		if(!updateSQL.contains(AbstractSQL.WHERE_S)) {
			return;
		}
		execute(connection,updateSQL);
	}
	
	public void update() throws SQLException {
		update(null);
	}
	
	/**
	 * delete
	 */ 
	
	public void delete(Connection connection) throws SQLException {
		String deleteSQL = processSQL.deleteSQL(this);
		if(!deleteSQL.contains(AbstractSQL.WHERE_S)) {
			return;
		}
		execute(connection,deleteSQL);
	}
	
	public void delete() throws SQLException {
		delete(null);
	}
	
	/**
	 * count
	 */
	public int count(Connection connection) throws SQLException {
		String executeSQL = processSQL.countSQL(this);
		return executeInt(connection,executeSQL);
	}

	public int count() throws SQLException {
		return count(null);
	}
	
	/**
	 * searchAndRetrieveList
	 */
	public List<Object>  searchAndRetrieveList(Connection connection){
		
		List<Object> retrieveList = null;

		Connection myConn = null;
		boolean isClose = true;
		
		Statement stmt = null;
		String executeSQL = processSQL.searchSQL(this);
		if (log.isDebugEnabled()) log.debug("searchSQL:" + executeSQL);
		
		try {
			retrieveList = new ArrayList<Object>();
			
			if(connection != null){
				myConn = connection;
				isClose = false;
			} else {
				myConn = DbConnectionManager.getConnection();
				isClose = true;
			}

			stmt = myConn.createStatement();
			ResultSet result = stmt.executeQuery(executeSQL);

			ResultSetMetaData resultSetMetaData = result.getMetaData();
			Object bean;
			int type;
			String columnName;

			while (result.next()) {

				bean = this.getClass().newInstance();

				for (int i = 1; i < resultSetMetaData.getColumnCount() + 1; i++) {
					type = resultSetMetaData.getColumnType(i);
					columnName = resultSetMetaData.getColumnName(i);

					if(type == Types.TIMESTAMP){
						DbBridgingBean.bindProperty(bean,
								StringUtils.splitToHump(columnName,prefix,false),
								result.getTimestamp(i));
					}else if(type == Types.DATE){
						DbBridgingBean.bindProperty(bean,
								StringUtils.splitToHump(columnName,prefix,false),
								result.getDate(i));
					}else if(type == Types.FLOAT){
						DbBridgingBean.bindProperty(this,
								StringUtils.splitToHump(columnName,prefix,true),
								result.getFloat(i));
					}else if(type == Types.SMALLINT){
						DbBridgingBean.bindProperty(bean,
								StringUtils.splitToHump(columnName,prefix,false),
								result.getShort(i));
					}else if(type == Types.INTEGER){
						DbBridgingBean.bindProperty(bean,
								StringUtils.splitToHump(columnName,prefix,false),
								result.getInt(i));
					}else if(type == Types.BIGINT){
						DbBridgingBean.bindProperty(bean,
								StringUtils.splitToHump(columnName,prefix,false),
								result.getLong(i));

					}else{
						DbBridgingBean.bindProperty(bean,
								StringUtils.splitToHump(columnName,prefix,false),
								result.getString(i));
					}

				}
				retrieveList.add(bean);
			}
			// addCache(doSql, retrieveList);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			JDBCUtil.release(stmt,myConn,isClose);
		}
		return retrieveList;
	}
	
	public List<Object> searchAndRetrieveList(){
		return searchAndRetrieveList(null);
	}

	/**
	 * retrieve
	 */
	public void retrieve(Connection connection) {
		
		Connection myConn = null;
		boolean isClose = true;
	
		Statement stmt = null;
		String executeSQL = processSQL.searchSQL(this);
		
		int index = executeSQL.indexOf("LIMIT");
		if(index>0)
			executeSQL = executeSQL.substring(0,executeSQL.indexOf("LIMIT"));

		executeSQL += " LIMIT 1";
		
		if (log.isDebugEnabled()) log.debug("retrieveSQL:" + executeSQL);

		try {
			if(connection != null){
				myConn = connection;
				isClose = false;
			} else {
				myConn = DbConnectionManager.getConnection();
				isClose = true;
			}
	
			stmt = myConn.createStatement();
			ResultSet result = stmt.executeQuery(executeSQL);
	
			ResultSetMetaData resultSetMetaData = result.getMetaData();
			int type;
			String columnName;
			while (result.next()) {
				for (int i = 1; i < resultSetMetaData.getColumnCount() + 1; i++) {
					type = resultSetMetaData.getColumnType(i);
					columnName = resultSetMetaData.getColumnName(i);

//					if (log.isDebugEnabled()) log.debug("type:" + type+"\tcolumnName:"+columnName);

					if(type == Types.TIMESTAMP || type == Types.DATE){
						DbBridgingBean.bindProperty(this,
								StringUtils.splitToHump(columnName,prefix,true),
								result.getTimestamp(i));
					}else if(type == Types.FLOAT){
						DbBridgingBean.bindProperty(this,
								StringUtils.splitToHump(columnName,prefix,true),
								result.getFloat(i));
					}else if(type == Types.SMALLINT){
						DbBridgingBean.bindProperty(this,
								StringUtils.splitToHump(columnName,prefix,true),
								result.getShort(i));
					}else if(type == Types.INTEGER){
						DbBridgingBean.bindProperty(this,
								StringUtils.splitToHump(columnName,prefix,true),
								result.getInt(i));
					}else if(type == Types.BIGINT){
						DbBridgingBean.bindProperty(this,
								StringUtils.splitToHump(columnName,prefix,true),
								result.getLong(i));
					}else if(type == Types.DOUBLE){
						DbBridgingBean.bindProperty(this,
								StringUtils.splitToHump(columnName,prefix,true),
								result.getDouble(i));
					}else {
						DbBridgingBean.bindProperty(this,
								StringUtils.splitToHump(columnName,prefix,true),
								result.getString(i));
					}
				}
			}
			
			// addCache(doSql, retrieveList);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			JDBCUtil.release(stmt,myConn,isClose);
		}
	}
	
	public void retrieve() throws SQLException {
		retrieve(null);
	}
}
