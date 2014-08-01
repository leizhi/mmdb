package com.mooo.mycoz.db.conf;

import java.util.Vector;

public class Mydb {

	private String defaultPool;
	private Vector<DbConnectionPool> pools;

	public String getDefaultPool() {
		return defaultPool;
	}

	public void setDefaultPool(String defaultPool) {
		this.defaultPool = defaultPool;
	}

	public void addPool(DbConnectionPool e) {
		if(pools==null) pools = new Vector<DbConnectionPool>();
		
		pools.add(e);
	}

	public Vector<DbConnectionPool> getPools() {
		return pools;
	}
}
