package com.mooo.mycoz.common;

import com.mooo.mycoz.db.conf.DbConf;
import com.mooo.mycoz.db.pool.DbConnectionManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.*;

public class IDGenerator {

	//获取数据库最大值
	private static final String SELECT_MAX_BY_TABLE = "SELECT MAX(ID) maxid FROM ";

	/**
	 *
	 * 释放JDBC资源
	 * @param result
	 *            结果集
	 * @param pstmt
	 *            执行对性
	 * @param notConn
	 *			是否关闭
	 * @param conn
	 *			JDBC连接
	 * @return void
	 */
	private synchronized static void release(ResultSet result,PreparedStatement pstmt,
											 boolean notConn,Connection conn){
		try {
			if (result != null)
				result.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}

		try {
			if (pstmt != null)
				pstmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}

		try {
			if (notConn) {
				if (conn != null)
					conn.close();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 *
	 * 获取表的主键下一个值
	 * @param conn
	 *            数据库类别
	 * @param table
	 *            表名
	 * @return int
	 */
	public synchronized static int getNextID(Connection conn, String table) {
		boolean notConn = false;

		PreparedStatement pstmt = null;
		ResultSet result = null;
		int nextId = 0;
		try {
			if (conn == null) {
				notConn = true;
				conn = DbConnectionManager.getConnection();
			}

			pstmt = conn.prepareStatement(SELECT_MAX_BY_TABLE + table);
			result = pstmt.executeQuery();
			while (result.next()) {
				nextId = result.getInt(1);
			}

			nextId++;
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			release(result,pstmt,notConn,conn);
		}
		return nextId;
	} // getNextID(String table)

	public static int getNextInt(String catalog, String table) {
		return getNextID(null, catalog + "." + table);
	}

	public static long getNextInt(String table) {
		return getNextID(null, table);
	}

	public static int getNextID(Connection conn, Class<?> clazz) {
		String catalog = DbConf.getInstance().getDbname(clazz.getPackage().getName());
		String table = StringUtils.humpToSplit(clazz.getSimpleName(), DbConf.getInstance().getDbHumpInterval());
		return getNextID(conn, catalog + "." + table);
	}

	public static int getNextInt(Class<?> clazz) {
		String catalog = DbConf.getInstance().getDbname(clazz.getPackage().getName());
		String table = StringUtils.humpToSplit(clazz.getSimpleName(), DbConf.getInstance().getDbHumpInterval());
		return getNextID(null, catalog + "." + table);
	}

	public static long getNextLong(Class<?> clazz) {
		return getNextInt(clazz);
	}

	/**
	 *
	 * 获取一组列表
	 * @param catalog
	 *            数据库类别
	 * @param table
	 *            表名
	 * @param key
	 *            唯一ID列
	 * @param value
	 *            对应的值列
	 * @return
	 */
	public synchronized static Map<Integer, String> getValues(String catalog,
			String table, String key, String value) {

		Connection conn = null;
		PreparedStatement pstmt = null;
		Map<Integer, String> values = new LinkedHashMap<Integer, String>();
		try {
			String sql = "SELECT ";

			if (StringUtils.isNull(key) || StringUtils.isNull(value)) {
				sql += "id,definition";
			} else {
				sql += key + "," + value;
			}

			if (!StringUtils.isNull(catalog)) {
				sql += " FROM " + catalog + ".";
			}

			if (!StringUtils.isNull(table)) {
				sql += table;
			}

			sql += " ORDER BY id";

			conn = DbConnectionManager.getConnection();
			pstmt = conn.prepareStatement(sql);

			ResultSet rs = pstmt.executeQuery();
			while (rs.next()) {
				values.put(rs.getInt(1), rs.getString(2));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (pstmt != null)
					pstmt.close();
				if (conn != null)
					conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}

		}
		return values;
	}

	public synchronized static Map<Integer, String> getValues(Class<?> clazz,
															  String key, String value) {
		String catalog = DbConf.getInstance().getDbname(clazz.getPackage().getName());
		String table = StringUtils.humpToSplit(clazz.getSimpleName(), DbConf.getInstance().getDbHumpInterval());
		return getValues(catalog, table, key, value);
	}

	public static Map<Integer, String> getValues(Class<?> clazz) {
		return getValues(clazz, "id", "definition");
	}

	/**
	 *
	 * 获取数据库表中序列号当前最大值
	 * @param conn
	 *            JDBC连接
	 * @param table
	 *            数据库表名
	 * @param field
	 *            生成序列号对应的表字段
	 * @param prefix
	 *            自定义前缀
	 * @return
	 */
	public synchronized static String getMaxPrefix(Connection conn,
												   String table, String field, String prefix) {
		boolean notConn = false;
		PreparedStatement pstmt = null;
		ResultSet result = null;

		String maxN = "";
		try {
			if (conn == null) {
				notConn = true;
				conn = DbConnectionManager.getConnection();
			}

			pstmt = conn.prepareStatement("SELECT MAX(" + field + ") nowCode FROM " + table + " WHERE " + field + " LIKE ?");
			pstmt.setString(1, prefix + "%");

			result = pstmt.executeQuery();
			while (result.next()) {
				maxN = result.getString(1);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			release(result,pstmt,notConn,conn);
		}
		return maxN;
	}

	/**
	 *
	 * 获取对象唯一序列号字段
	 * @param conn
	 *            JDBC连接
	 * @param clazz
	 *            数据库表对应的类
	 * @param field
	 *            生成序列号对应的表字段
	 * @param prefix
	 *            自定义前缀
	 * @param snMax
	 *            序列号的最大位数
	 * @param snLength
	 *            当前位数
	 * @return
	 */
	public static String getSN(Connection conn,Class<?> clazz,String field,String prefix,int snMax,int snLength) {

		String sn = "";
		for(int i=0;i<snLength;i++){
			sn +="0";
		}
		
		DecimalFormat df = new DecimalFormat(sn); 

		String exitsSN = getMaxPrefix(conn,clazz.getSimpleName(), field, prefix);
		
       if(exitsSN!=null && exitsSN.length()==snMax){
			sn = exitsSN.substring(snMax-snLength);
			int number = new Integer(sn);
			
			number++;
			
			sn = df.format(number);
        }else{
			sn = df.format(1);
        }
        
        return prefix+sn;
	}

	/**
	 *
	 * 获取对象唯一序列号字段
	 * @param conn
	 *            JDBC连接
	 * @param clazz
	 *            数据库表对应的类
	 * @param field
	 *            生成序列号对应的表字段
	 * @param prefix
	 *            自定义前缀
	 * @return
	 */
	public static String getSN(Connection conn,Class<?> clazz,String field,String prefix) {
		return getSN(conn,clazz,field,prefix,16,4);
	}
}
