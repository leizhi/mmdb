package com.mooo.mycoz.db.conf;

import java.util.Vector;

public class DbConnectionPool {

	private String poolname;
	private String driver;
	private String server;
	private String username;
	private String password;
	private String dbsql;
	private String dbHumpInterval;
	private double connectionTimeout;
	private int minConnections;
	private int maxConnections;
	private String connectionProvider;

	private Vector<DbPack> dbPacks;

	public String getPoolname() {
		return poolname;
	}

	public void setPoolname(String poolname) {
		this.poolname = poolname;
	}

	public String getDriver() {
		return driver;
	}

	public void setDriver(String driver) {
		this.driver = driver;
	}

	public String getServer() {
		return server;
	}

	public void setServer(String server) {
		this.server = server;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getDbsql() {
		return dbsql;
	}

	public void setDbsql(String dbsql) {
		this.dbsql = dbsql;
	}

	public String getDbHumpInterval() {
		return dbHumpInterval;
	}

	public void setDbHumpInterval(String dbHumpInterval) {
		this.dbHumpInterval = dbHumpInterval;
	}

	public double getConnectionTimeout() {
		return connectionTimeout;
	}

	public void setConnectionTimeout(double connectionTimeout) {
		this.connectionTimeout = connectionTimeout;
	}

	public int getMinConnections() {
		return minConnections;
	}

	public void setMinConnections(int minConnections) {
		this.minConnections = minConnections;
	}

	public int getMaxConnections() {
		return maxConnections;
	}

	public void setMaxConnections(int maxConnections) {
		this.maxConnections = maxConnections;
	}

	public String getConnectionProvider() {
		return connectionProvider;
	}

	public void setConnectionProvider(String connectionProvider) {
		this.connectionProvider = connectionProvider;
	}

	public void addDbPack(DbPack e) {
		if (dbPacks == null)
			dbPacks = new Vector<DbPack>();

		dbPacks.add(e);
	}

	public Vector<DbPack> getDbPacks() {
		return dbPacks;
	}
	
}
