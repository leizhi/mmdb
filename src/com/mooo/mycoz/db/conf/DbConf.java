package com.mooo.mycoz.db.conf;

import org.apache.commons.digester.Digester;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Vector;

public class DbConf {
	private static Log log = LogFactory.getLog(DbConf.class);

	private static Object initLock = new Object();
	private static DbConf conf = null;

	private static String confFile = "mydb.xml";
	private static Mydb mydb;

	/*
	 * single
	 */
	public static DbConf getInstance() {
		if(conf==null){
			synchronized (initLock) {
				conf = new DbConf();
			}
		}
		return conf;
	}
	
	/*
	 * initialization
	 */
	
	private DbConf(){
		try {
			Digester digester = new Digester();
			digester.setValidating(false);

			digester.addObjectCreate("Mydb", Mydb.class);
			digester.addSetProperties("Mydb", "defaultPool","defaultPool"); 

			digester.addObjectCreate("Mydb/DbConnectionPool", DbConnectionPool.class);
			digester.addSetProperties("Mydb/DbConnectionPool", "poolname","poolname"); 
			digester.addSetProperties("Mydb/DbConnectionPool", "driver","driver"); 
			digester.addSetProperties("Mydb/DbConnectionPool", "server","server"); 
			digester.addSetProperties("Mydb/DbConnectionPool", "username","username"); 
			digester.addSetProperties("Mydb/DbConnectionPool", "password","password"); 
			digester.addSetProperties("Mydb/DbConnectionPool", "dbsql","dbsql"); 
			digester.addSetProperties("Mydb/DbConnectionPool", "dbHumpInterval","dbHumpInterval");
			digester.addSetProperties("Mydb/DbConnectionPool", "connectionTimeout","connectionTimeout"); 
			digester.addSetProperties("Mydb/DbConnectionPool", "minConnections","minConnections"); 
			digester.addSetProperties("Mydb/DbConnectionPool", "maxConnections","maxConnections"); 
			digester.addSetProperties("Mydb/DbConnectionPool", "connectionProvider","connectionProvider"); 

			digester.addObjectCreate("Mydb/DbConnectionPool/DbPack", DbPack.class);
			digester.addSetProperties("Mydb/DbConnectionPool/DbPack", "dbpath","dbpath");
			digester.addSetProperties("Mydb/DbConnectionPool/DbPack", "dbname","dbname");
			digester.addSetNext("Mydb/DbConnectionPool/DbPack", "addDbPack");
			
			digester.addSetNext("Mydb/DbConnectionPool", "addPool");

			File conff = new File(confFile);
			InputStream confStream = null;
			if(conff.exists())
				confStream = new FileInputStream(conff);
			else
				confStream = this.getClass().getClassLoader().getResourceAsStream(confFile);

			if(confStream!=null)
				mydb = (Mydb) digester.parse(confStream);
			else
				mydb = new Mydb();

			if(log.isDebugEnabled())log.debug("DbConf");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public Mydb getMydb(){
		return mydb;
	}

	public DbConnectionPool getDbConnectionPool(String poolName){
		
		Vector<DbConnectionPool> pools = mydb.getPools();
		for(DbConnectionPool dcpool:pools){
			if(dcpool.getPoolname().equals(poolName))
				return dcpool;
		}
		return null;
	}
	
	public String getDbsql(String poolName){
		
		Vector<DbConnectionPool> pools = mydb.getPools();
		for(DbConnectionPool dcpool:pools){
			if(dcpool.getPoolname().equals(poolName))
				return dcpool.getDbsql();
		}
		return null;
	}
	
	public String getDbname(String poolName,String dbpath){
		
		Vector<DbConnectionPool> pools = mydb.getPools();
		for(DbConnectionPool dcpool:pools){
			if(dcpool.getPoolname().equals(poolName))
				for(DbPack dbPack:dcpool.getDbPacks()){
					if(dbPack.getDbpath().equals(dbpath))
						return dbPack.getDbname();
				}
		}
		return null;
	}
	
	public String getDbname(String dbpath){
		return getDbname(mydb.getDefaultPool(),dbpath);
	}
	
	public String getDbHumpInterval(String poolName){
		
		Vector<DbConnectionPool> pools = mydb.getPools();
		for(DbConnectionPool dcpool:pools){
			if(dcpool.getPoolname().equals(poolName))
				return dcpool.getDbHumpInterval();
		}
		return null;
	}
	
	public String getDbHumpInterval(){
		return getDbHumpInterval(mydb.getDefaultPool());
	}


}
