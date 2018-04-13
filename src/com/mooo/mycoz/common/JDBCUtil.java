package com.mooo.mycoz.common;


import java.sql.*;

public class JDBCUtil{
    /**
     *
     * 释放JDBC资源
     * @param result
     *            结果集
     * @param preparedStatement
     *            执行对性
     * @param notConn
     *			是否关闭
     * @param conn
     *			JDBC连接
     * @return void
     */
    public synchronized static void release(ResultSet result, PreparedStatement preparedStatement,
                                             boolean notConn, Connection conn){
        try {
            if (result != null)
                result.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        try {
            if (preparedStatement != null)
                preparedStatement.close();
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

    public synchronized static void release(Connection myConn,boolean isClose){
        try {
            if(isClose)
                myConn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public synchronized static void release(Statement stmt,Connection myConn,boolean isClose){
        try {
            if (stmt != null)
                stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        release(myConn,isClose);
    }

    public synchronized static void release(ResultSet result,Statement stmt,Connection myConn,boolean isClose){
        try {
            if (result != null)
                result.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        release(stmt,myConn,isClose);
    }
}