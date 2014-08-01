package com.mooo.mycoz.db.sql;

import com.mooo.mycoz.db.conf.DbConf;

public class SQLFactory {
	private static Object initLock = new Object();
	
	private static String className = "com.mooo.mycoz.db.sql.MysqlSQL";
	private static ProcessSQL factory = null;

	public static ProcessSQL getInstance(String poolName) {
		synchronized (initLock) {
			String classNameProp = DbConf.getInstance().getDbsql(poolName);
			if (classNameProp != null) {
				className = classNameProp;
			}
			try {
				// Load the class and create an instance.
				Class<?> c = Class.forName(className);
				factory = (ProcessSQL) c.newInstance();
			} catch (Exception e) {
				System.err.println("Failed to load ForumFactory class " + className
						+ ". Yazd cannot function normally.");
				e.printStackTrace();
				return null;
			}
		}
		return factory;
	}
	
	public static ProcessSQL getInstance() {
		return getInstance(DbConf.getInstance().getMydb().getDefaultPool());
	}
}
