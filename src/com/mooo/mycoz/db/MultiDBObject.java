package com.mooo.mycoz.db;

import com.mooo.mycoz.common.JDBCUtil;
import com.mooo.mycoz.common.StringUtils;
import com.mooo.mycoz.db.conf.DbConf;
import com.mooo.mycoz.db.pool.DbConnectionManager;
import com.mooo.mycoz.db.sql.MysqlMultiSQL;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class MultiDBObject extends MysqlMultiSQL implements MultiDbProcess {
	
	private static Log log = LogFactory.getLog(MultiDBObject.class);

	private DBExecute dbExecute;

	public  MultiDBObject(){
		dbExecute = new DBExecute();
	}

	/**
	 * 
	 */
	public synchronized List<Object> searchAndRetrieveList(Connection connection){
		long startTime = System.currentTimeMillis();

		List<Object> retrieveList = null;
		String doSql = searchSQL();
		if (log.isDebugEnabled()) log.debug("searchSQL:" + doSql);

		Connection myConn = null;
		Statement stmt = null;
		boolean isClose = true;

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
			ResultSet result = stmt.executeQuery(doSql);
			ResultSetMetaData rsmd = result.getMetaData();

			String key;
			String catalog,table,column;
			String aliasT,columnLabel;
			
			while (result.next()) {
				
				Map<String, Object> allRow = new HashMap<String, Object>();
				for (Entry<String, Class<?>>  entry:objs.entrySet()) {
					key = entry.getKey();
					
					Class<?> cls = objs.get(key);
					Object bean = cls.newInstance();
					
					allRow.put(key, bean);
				}

				for (int i=1; i < rsmd.getColumnCount()+1; i++) {
					
					catalog = rsmd.getCatalogName(i);
					table = rsmd.getTableName(i);

					columnLabel = rsmd.getColumnLabel(i);
					
					aliasT = columnLabel.substring(0,columnLabel.indexOf('_'));
					column = columnLabel.substring(columnLabel.indexOf('_')+1);

					int type = DbUtil.type(myConn,catalog,table,StringUtils.splitToHump(column,DbConf.getInstance().getDbHumpInterval(),true));
					
					if(allRow.containsKey(aliasT)){
						Object bean = allRow.get(aliasT);
						
						if(type == Types.TIMESTAMP){
							DbBridgingBean.bindProperty(bean,StringUtils.splitToHump(column,DbConf.getInstance().getDbHumpInterval(),true),result.getTimestamp(i));
						}else {
							DbBridgingBean.bindProperty(bean,StringUtils.splitToHump(column,DbConf.getInstance().getDbHumpInterval(),true),result.getString(i));
						}					
					}					
				}
				retrieveList.add(allRow);
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
	
	/**
	 * 
	 */
	public List<Object> searchAndRetrieveList(){
		return searchAndRetrieveList(null);
	}
	
	public int count() throws SQLException {
		return count(null);
	}
	
	public int count(Connection connection) throws SQLException {
		long startTime = System.currentTimeMillis();

		String doSql = buildCountSQL();
		
		int total= dbExecute.executeInt(connection,doSql);

		long finishTime = System.currentTimeMillis();
		long hours = (finishTime - startTime) / 1000 / 60 / 60;
		long minutes = (finishTime - startTime) / 1000 / 60 - hours * 60;
		long seconds = (finishTime - startTime) / 1000 - hours * 60 * 60 - minutes * 60;
		
		if (log.isDebugEnabled()) log.debug(finishTime - startTime);
		if (log.isDebugEnabled()) log.debug("count expends:   " + hours + ":" + minutes + ":" + seconds);
		return total;
	}

	/**
	 *
	 */
	public List<Map> executeAndRetrieveList(Connection connection,String executeSQL){
		return dbExecute.executeAndRetrieveList(connection,executeSQL);
	}

	public List<Map> executeAndRetrieveList(String executeSQL){
		return executeAndRetrieveList(null,executeSQL);
	}
}