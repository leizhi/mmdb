package com.mooo.mycoz.db;

import com.mooo.mycoz.common.JDBCUtil;
import com.mooo.mycoz.db.pool.DbConnectionManager;

import java.sql.*;

public class DBExecute {

	public int execute(Connection connection,String executeSQL) throws SQLException {
		int total=0;

		Connection myConn = null;
		boolean isClose = true;

		Statement stmt = null;

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
			JDBCUtil.release(stmt,myConn,isClose);
		}
		return total;
	}
}