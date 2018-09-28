package com.mooo.mycoz.db;

import com.mooo.mycoz.common.JDBCUtil;
import com.mooo.mycoz.db.pool.DbConnectionManager;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DBExecute {
	private static Log log = LogFactory.getLog(DBObject.class);

	public int executeInt(Connection connection, String executeSQL) throws SQLException {
		if (log.isDebugEnabled()) log.debug("executeSQL:" + executeSQL);

		int total = 0;

		Connection myConn = null;
		boolean isClose = true;

		Statement stmt = null;

		try {
			if (connection != null) {
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
			JDBCUtil.release(stmt, myConn, isClose);
		}
		return total;
	}


	public void execute(Connection connection, String executeSQL) throws SQLException {

		if (log.isDebugEnabled()) log.debug("executeSQL:" + executeSQL);

		Connection myConn = null;
		boolean isClose = true;

		Statement stmt = null;
		try {
			if (connection != null) {
				myConn = connection;
				isClose = false;
			} else {
				myConn = DbConnectionManager.getConnection();
				isClose = true;
			}

			stmt = myConn.createStatement();
			stmt.execute(executeSQL);
		} finally {
			JDBCUtil.release(stmt, myConn, isClose);
		}
	}

	public void execute(String executeSQL) throws SQLException {
		execute(null,executeSQL);
	}

	/**
	 *
	 */
	public List<Map> executeAndRetrieveList(Connection connection, String executeSQL){

		long startTime = System.currentTimeMillis();

		List<Map> retrieveList = null;

		if (log.isDebugEnabled()) log.debug("searchSQL:" + executeSQL);

		Connection myConn = null;
		Statement stmt = null;
		boolean isClose = true;

		try {
			retrieveList = new ArrayList();

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

			String column;
			Object value;
			while (result.next()) {

				Map<String, Object> row = new HashMap<String, Object>();

				for (int i=1; i < resultSetMetaData.getColumnCount()+1; i++) {
					column = resultSetMetaData.getColumnLabel(i);
					value = result.getObject(i);

//					System.out.println(column+"\t"+value);

					row.put(column,value);
				}
				retrieveList.add(row);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			JDBCUtil.release(stmt,myConn,isClose);
		}

		long finishTime = System.currentTimeMillis();
		long hours = (finishTime - startTime) / 1000 / 60 / 60;
		long minutes = (finishTime - startTime) / 1000 / 60 - hours * 60;
		long seconds = (finishTime - startTime) / 1000 - hours * 60 * 60 - minutes * 60;

		if (log.isDebugEnabled()) log.debug(finishTime - startTime);
		if (log.isDebugEnabled()) log.debug("search expends:   " + hours + ":" + minutes + ":" + seconds);
		return retrieveList;
	}

	public List<Map> executeAndRetrieveList(String executeSQL){
		return executeAndRetrieveList(null,executeSQL);
	}
}