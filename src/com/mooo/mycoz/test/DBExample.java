package com.mooo.mycoz.test;

import com.mooo.mycoz.db.DbBridgingBean;
import com.mooo.mycoz.db.MultiDBObject;
import com.mooo.mycoz.db.conf.DbConf;
import com.mooo.mycoz.db.conf.DbConnectionPool;
import com.mooo.mycoz.db.conf.Mydb;
import com.mooo.mycoz.db.pool.DbConnectionManager;

import java.sql.*;
import java.util.Vector;

public class DBExample {
	
	public void buildInsert(String table){
		Connection con = null;
		Statement stmt = null;
		ResultSetMetaData rsmd = null;
		String sql = "";
		try {
			System.out.println("打开连接-------------");

		ResultSet rs = null;
		//mypool
		con = DbConnectionManager.getConnection();
		System.out.println("打开连接-------------");
		System.out.println(con);
		
		sql = "SELECT  * FROM "+table;
		System.out.println(sql);

		stmt = con.createStatement();
		rs = stmt.executeQuery(sql);
		rsmd = rs.getMetaData();
		String columnName="";

		for (int i = 0; i < rsmd.getColumnCount(); i++) {
			columnName = rsmd.getColumnName(i + 1).toLowerCase();
			System.out.print(columnName + ",");
		}
		System.out.println();
		
		} catch (Exception e) {
			e.printStackTrace();
			try {
				con.rollback();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
			System.out.println("Exception: " + e.getMessage());

		} finally {
			try {
				stmt.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			try {
				con.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	public static void DbConfTest() {
//        System.out.println("Hello World!");

		Mydb mydb = DbConf.getInstance().getMydb();

		Vector<DbConnectionPool> pools = mydb.getPools();

		for(DbConnectionPool pool:pools){
			System.out.println("pool->>>"+pool);
			System.out.println("pool->>>"+pool.getPoolname());
		}
	}

	public static void dbToBean() {
		DbBridgingBean bd = new DbBridgingBean();
		bd.dbToBean("smdpack","task_info");
	}

	public static void confInit() {
		dbToBean();
	}

	public static void multiDB() {
		try {
			MultiDBObject multiDBObject = new MultiDBObject();
//			multiDBObject.addTable(RiskTheme.class, "riskTheme");
//			multiDBObject.addTable(RiskTheme.class, "riskTheme1");
//			multiDBObject.setForeignKey("riskTheme","id","riskTheme1","id");
//			multiDBObject.setRetrieveField("riskTheme","themeId");
//
//			List ms = multiDBObject.searchAndRetrieveList();
//			for(Object obj:ms){
//				Map map = (Map)obj;
//				RiskTheme riskTheme = (RiskTheme)map.get("riskTheme");
//				System.out.println(riskTheme.getThemeId());
//			}
/*
			multiDBObject = new MultiDBObject();
			multiDBObject.addTable(User.class, "riskTheme");
			multiDBObject.addTable(User.class, "riskTheme1");
			multiDBObject.setForeignKey("riskTheme","id","riskTheme1","id");
			multiDBObject.setRetrieveField("riskTheme","id");
			multiDBObject.setRetrieveField("riskTheme","name");

			List ms = multiDBObject.searchAndRetrieveList();
			for(Object obj:ms){
				Map map = (Map)obj;
				User riskTheme = (User) map.get("riskTheme");
				System.out.println(riskTheme.getName());
			}
*/
//			RiskTheme riskTheme = new RiskTheme();
//			riskTheme.count();
//			List rs = riskTheme.searchAndRetrieveList();
//			for(Object obj:rs){
//				riskTheme = (RiskTheme)obj;
//				System.out.println(riskTheme.getId());
//			}
// 			User user = new User();
//			List rs = user.searchAndRetrieveList();
//			for(Object obj:rs){
//				user = (User)obj;
//				System.out.println(user.getName());
//			}
//		System.out.println(StringUtils.humpToSplit(RiskTheme.class.getSimpleName(),"Case"));

		}catch (Exception e){
			e.printStackTrace();
		}
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		dbToBean();
	}

//	public static String humpToPre(String word){
//		String a1 = "";
//		boolean isLower = Character.isLowerCase(word.charAt(0));
//
//		for(int i = 0; i < word.length(); i++){
//			char c = word.charAt(i);
//
//			if(isLower && Character.isLowerCase(c)!=isLower) {
//				a1 += "_";
//			}
//			isLower = Character.isLowerCase(word.charAt(i));
//
//			a1 += Character.toLowerCase(c);;
//		}
//		return a1;
//	}
//
//	public static String preToHump(String word,char p,boolean firstUpper){
//		String a1 = "";
//		boolean toUpper = true;
//
//		for(int i = 0; i < word.length(); i++){
//			char c = word.charAt(i);
//
//			if(i==0)
//				if(firstUpper)
//					c = Character.toUpperCase(word.charAt(0));
//				else
//					c = Character.toLowerCase(word.charAt(0));
//
//			if (c==p){
//				toUpper = true;
//				continue;
//			}else{
//				if(toUpper) {
//					c = Character.toUpperCase(c);
//					toUpper = false;
//				}
//			}
//
//			a1 += c;
//		}
//		return a1;
//	}
}
