package com.mooo.mycoz.db;

import java.sql.Connection;

import com.mooo.mycoz.db.pool.DbConnectionManager;

public class Transaction {

	private Connection connection;
	private boolean supportsTransactions;

	public synchronized void start() {
		try {
				connection = DbConnectionManager.getConnection();
				supportsTransactions = connection.getMetaData().supportsTransactions();
				
				if (supportsTransactions) {
					connection.setAutoCommit(false);
				}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public boolean isTransactional() {
		return supportsTransactions;
	}

	public Connection getConnection() {
		return connection;
	}

	public synchronized void rollback() {
		try {
			if (supportsTransactions) {
				if (connection != null)
					connection.rollback();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public synchronized void commit() {
		try {
			if (supportsTransactions) {
				if (connection != null)
					connection.commit();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public synchronized void end() {
		try {
			if (supportsTransactions) {
					connection.setAutoCommit(true);
			}

			if (connection != null) {
				connection.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
