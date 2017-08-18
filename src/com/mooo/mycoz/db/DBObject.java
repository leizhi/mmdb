package com.mooo.mycoz.db;

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

public class DBObject<T> implements DbProcess{

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

	private void execute(Connection connection,String executeSQL) throws SQLException {

		if (log.isDebugEnabled()) log.debug("executeSQL:" + executeSQL);

		Connection myConn = null;
		boolean isClose = true;

		Statement stmt = null;
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

	/**
	 * add
	 */ 
	public synchronized void add(Connection connection) throws SQLException {
		String addSQL = processSQL.addSQL(this);
		if (log.isDebugEnabled()) log.debug("addSQL:" + addSQL);
		execute(connection,addSQL);
	}
	
	public void add() throws SQLException {
		add(null);
	}

	/**
	 * update
	 */ 
	public synchronized void update(Connection connection) throws SQLException {
		String updateSQL = processSQL.updateSQL(this);
		if (log.isDebugEnabled()) log.debug("updateSQL:" + updateSQL);
		if(updateSQL.indexOf(AbstractSQL.WHERE_S) < 0) return;

		execute(connection,updateSQL);
	}
	
	public void update() throws SQLException {
		update(null);
	}
	
	/**
	 * delete
	 */ 
	
	public synchronized void delete(Connection connection) throws SQLException {
		String deleteSQL = processSQL.deleteSQL(this);
		if (log.isDebugEnabled()) log.debug("deleteSQL:" + deleteSQL);
		if(deleteSQL.indexOf(AbstractSQL.WHERE_S) < 0) return;

		execute(connection,deleteSQL);
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
			
			if (result.next()) {
				total = result.getInt(1);
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
								StringUtils.splitToHump(rsmd.getColumnName(i),prefix,false),
								result.getTimestamp(i));
					}else if(type == Types.DATE){
						DbBridgingBean.bindProperty(bean,
								StringUtils.splitToHump(rsmd.getColumnName(i),prefix,false),
								result.getDate(i));
					}else if(type == Types.FLOAT){
						DbBridgingBean.bindProperty(this,
								StringUtils.splitToHump(rsmd.getColumnName(i),prefix,true),
								result.getFloat(i));
					}else if(type == Types.SMALLINT){
						DbBridgingBean.bindProperty(bean,
								StringUtils.splitToHump(rsmd.getColumnName(i),prefix,false),
								result.getShort(i));
					}else if(type == Types.INTEGER){
						DbBridgingBean.bindProperty(bean,
								StringUtils.splitToHump(rsmd.getColumnName(i),prefix,false),
								result.getInt(i));
					}else if(type == Types.BIGINT){
						DbBridgingBean.bindProperty(bean,
								StringUtils.splitToHump(rsmd.getColumnName(i),prefix,false),
								result.getLong(i));
					}else{
						DbBridgingBean.bindProperty(bean,
								StringUtils.splitToHump(rsmd.getColumnName(i),prefix,false),
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
								StringUtils.splitToHump(rsmd.getColumnName(i),prefix,true),
								result.getTimestamp(i));
					}else if(type == Types.FLOAT){
						DbBridgingBean.bindProperty(this,
								StringUtils.splitToHump(rsmd.getColumnName(i),prefix,true),
								result.getFloat(i));
					}else if(type == Types.SMALLINT){
						DbBridgingBean.bindProperty(this,
								StringUtils.splitToHump(rsmd.getColumnName(i),prefix,true),
								result.getShort(i));
					}else if(type == Types.INTEGER){
						DbBridgingBean.bindProperty(this,
								StringUtils.splitToHump(rsmd.getColumnName(i),prefix,true),
								result.getInt(i));
					}else if(type == Types.BIGINT){
						DbBridgingBean.bindProperty(this,
								StringUtils.splitToHump(rsmd.getColumnName(i),prefix,true),
								result.getLong(i));
					}else if(type == Types.DOUBLE){
						DbBridgingBean.bindProperty(this,
								StringUtils.splitToHump(rsmd.getColumnName(i),prefix,true),
								result.getDouble(i));
					}else {
						DbBridgingBean.bindProperty(this,
								StringUtils.splitToHump(rsmd.getColumnName(i),prefix,true),
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
