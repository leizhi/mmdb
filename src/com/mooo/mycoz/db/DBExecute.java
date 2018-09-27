package com.mooo.mycoz.db;

import com.mooo.mycoz.common.JDBCUtil;
import com.mooo.mycoz.db.pool.DbConnectionManager;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

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
}