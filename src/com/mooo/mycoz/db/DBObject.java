package com.mooo.mycoz.db;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.mooo.mycoz.common.StringUtils;
import com.mooo.mycoz.db.pool.DbConnectionManager;
import com.mooo.mycoz.db.sql.SQLFactory;
import com.mooo.mycoz.db.sql.ProcessSQL;

public class DBObject implements DbProcess{

	private static Log log = LogFactory.getLog(DBObject.class);

	/**
	 * database operation method
	 */

	private ProcessSQL processSQL=null;
	
	public DBObject(){
		processSQL=SQLFactory.getInstance();
	}
	
	/**
	 * add
	 */ 
	public synchronized void add(Connection connection) throws SQLException {
		Connection myConn = null;
		boolean isClose = true;
		
		Statement stmt = null;
		
		String executeSQL = processSQL.addSQL(this);
		
		if (log.isDebugEnabled()) log.debug("executeSQL:" + executeSQL);

		try{
			if(connection != null){
				myConn = connection;
				isClose = false;
			} else {
				myConn = DbConnectionManager.getConnection();
				isClose = true;
			}
			
			stmt = myConn.createStatement();
			stmt.execute(executeSQL);
		}finally {
	
			try {
					stmt.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			
			try {
				if(isClose)
					myConn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void add() throws SQLException {
		add(null);
	}
	
	/**
	 * update
	 */ 
	public synchronized void update(Connection connection) throws SQLException {
		
		Connection myConn = null;
		boolean isClose = true;
		
		Statement stmt = null;
		String executeSQL = processSQL.updateSQL(this);
		if (log.isDebugEnabled()) log.debug("executeSQL:" + executeSQL);
		try{
			if(connection != null){
				myConn = connection;
				isClose = false;
			} else {
				myConn = DbConnectionManager.getConnection();
				isClose = true;
			}
			
			stmt = myConn.createStatement();
			stmt.execute(executeSQL);
		}finally {
	
			try {
					stmt.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			
			try {
				if(isClose)
					myConn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}		
	}
	
	public void update() throws SQLException {
		update(null);
	}
	
	/**
	 * delete
	 */ 
	
	public synchronized void delete(Connection connection) throws SQLException {
		
		Connection myConn = null;
		boolean isClose = true;
		
		Statement stmt = null;
		String executeSQL = processSQL.deleteSQL(this);
		if (log.isDebugEnabled()) log.debug("executeSQL:" + executeSQL);
		try{
			if(connection != null){
				myConn = connection;
				isClose = false;
			} else {
				myConn = DbConnectionManager.getConnection();
				isClose = true;
			}
			
			stmt = myConn.createStatement();
			stmt.execute(executeSQL);
		}finally {
	
			try {
					stmt.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			
			try {
				if(isClose)
					myConn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
	}
	
	public void delete() throws SQLException {
		delete(null);
	}
	
	/**
	 * count
	 */
	public synchronized int count(Connection connection) throws SQLException {
		int total=0;

		Connection myConn = null;
		boolean isClose = true;
		
		Statement stmt = null;
		String executeSQL = processSQL.countSQL(this);
		if (log.isDebugEnabled()) log.debug("countSQL:" + executeSQL);
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
			
			while (result.next()) {
				total ++;
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
					stmt.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			
			try {
				if(isClose)
					myConn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}			
		}
		return total;
	}
	
	
	public int count() throws SQLException {
		return count(null);
	}
	
	/**
	 * searchAndRetrieveList
	 */
	public synchronized List<Object>  searchAndRetrieveList(Connection connection)
			throws SQLException {
		
		List<Object> retrieveList = null;

		Connection myConn = null;
		boolean isClose = true;
		
		Statement stmt = null;
		String executeSQL = processSQL.searchSQL(this);
		if (log.isDebugEnabled()) log.debug("executeSQL:" + executeSQL);
		
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

			ResultSetMetaData rsmd = result.getMetaData();
			Object bean;
			int type=0;

			while (result.next()) {

				bean = this.getClass().newInstance();

				for (int i = 1; i < rsmd.getColumnCount() + 1; i++) {
					type = rsmd.getColumnType(i);
					
					if(type == Types.TIMESTAMP){
						DbBridgingBean.bindProperty(bean,
								StringUtils.formatHump(rsmd.getColumnName(i),null),
								result.getTimestamp(i));
					}else if(type == Types.DATE){
						DbBridgingBean.bindProperty(bean,
								StringUtils.formatHump(rsmd.getColumnName(i),null),
								result.getDate(i));	
					}else if(type == Types.SMALLINT){
						DbBridgingBean.bindProperty(bean,
								StringUtils.formatHump(rsmd.getColumnName(i),null),
								result.getShort(i));
					}else if(type == Types.INTEGER){
						DbBridgingBean.bindProperty(bean,
								StringUtils.formatHump(rsmd.getColumnName(i),null),
								result.getInt(i));
					}else if(type == Types.BIGINT){
						DbBridgingBean.bindProperty(bean,
								StringUtils.formatHump(rsmd.getColumnName(i),null),
								result.getLong(i));
					}else{
						DbBridgingBean.bindProperty(bean,
								StringUtils.formatHump(rsmd.getColumnName(i),null),
								result.getString(i));
					}

				}
				retrieveList.add(bean);
			}
			// addCache(doSql, retrieveList);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {

			try {
					stmt.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}

			try {
				if(isClose)
					myConn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return retrieveList;
	}
	
	public List<Object> searchAndRetrieveList()
			throws SQLException {
		return searchAndRetrieveList(null);
	}
	
	/**
	 * retrieve
	 */
	public synchronized void retrieve(Connection connection) throws SQLException {
		
		Connection myConn = null;
		boolean isClose = true;
	
		Statement stmt = null;
		String executeSQL = processSQL.searchSQL(this);
		
		int index = executeSQL.indexOf("LIMIT");
		if(index>0)
			executeSQL = executeSQL.substring(0,executeSQL.indexOf("LIMIT"));
		
		executeSQL += " LIMIT 1";
		
		if (log.isDebugEnabled()) log.debug("executeSQL:" + executeSQL);

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
	
			ResultSetMetaData rsmd = result.getMetaData();
			int type=0;
			
			while (result.next()) {
				for (int i = 1; i < rsmd.getColumnCount() + 1; i++) {
					type = rsmd.getColumnType(i);
					if(type == Types.TIMESTAMP || type == Types.DATE){
						DbBridgingBean.bindProperty(this,
								StringUtils.prefixToUpper(rsmd.getColumnName(i),null,true),
								result.getTimestamp(i));
					}else if(type == Types.INTEGER || type == Types.SMALLINT){
						DbBridgingBean.bindProperty(this,
								StringUtils.prefixToUpper(rsmd.getColumnName(i),null,true),
								result.getInt(i));
					}else if(type == Types.BIGINT){
						DbBridgingBean.bindProperty(this,
								StringUtils.prefixToUpper(rsmd.getColumnName(i),null,true),
								result.getLong(i));
					}else if(type == Types.DOUBLE){
						DbBridgingBean.bindProperty(this,
								StringUtils.prefixToUpper(rsmd.getColumnName(i),null,true),
								result.getDouble(i));
					}else if(type == Types.FLOAT){
						DbBridgingBean.bindProperty(this,
								StringUtils.prefixToUpper(rsmd.getColumnName(i),null,true),
								result.getFloat(i));
					}else {
						DbBridgingBean.bindProperty(this,
								StringUtils.prefixToUpper(rsmd.getColumnName(i),null,true),
								result.getString(i));
					}
				}
			}
			
			// addCache(doSql, retrieveList);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
	
			try {
				if (stmt != null)
					stmt.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
	
			try {
				if(isClose)
					myConn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void retrieve() throws SQLException {
		retrieve(null);
	}

	public void setField(String fieldName, Object fieldValue, int fieldType,
			boolean isPrimaryKey) {
		processSQL.setField(fieldName, fieldValue, fieldType, isPrimaryKey);
	}

	public void setLike(String fieldName, Object fieldValue) {
		processSQL.setLike(fieldName, fieldValue);
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
}
