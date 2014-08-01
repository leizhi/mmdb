/**
 * Copyright (C) 2001 Yasna.com. All rights reserved.
 *
 * ===================================================================
 * The Apache Software License, Version 1.1
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgment:
 *       "This product includes software developed by
 *        Yasna.com (http://www.yasna.com)."
 *    Alternately, this acknowledgment may appear in the software itself,
 *    if and wherever such third-party acknowledgments normally appear.
 *
 * 4. The names "Yazd" and "Yasna.com" must not be used to
 *    endorse or promote products derived from this software without
 *    prior written permission. For written permission, please
 *    contact yazd@yasna.com.
 *
 * 5. Products derived from this software may not be called "Yazd",
 *    nor may "Yazd" appear in their name, without prior written
 *    permission of Yasna.com.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL YASNA.COM OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of Yasna.com. For more information
 * on Yasna.com, please see <http://www.yasna.com>.
 */


package com.mooo.mycoz.db.pool;

import java.sql.Connection;
import java.util.Vector;

import com.mooo.mycoz.db.conf.DbConf;
import com.mooo.mycoz.db.conf.DbConnectionPool;

/**
 * Central manager of database connections. All methods are static so that they
 * can be easily accessed throughout the classes in the database package.
 */
public class DbConnectionManager {

    private static DbConnectionManager dbPoolManager;
    private static Object providerLock = new Object();

	private static Vector<ConnectionProvider> connectionProviders = new Vector<ConnectionProvider>();

    private DbConnectionManager(){}
    
    public static ConnectionProvider getDbConnectionPool(String poolName){
		for(ConnectionProvider connectionProvider:connectionProviders){
			if(connectionProvider.getDbConnectionPool().getPoolname().equals(poolName))
				return connectionProvider;
		}
		
		return null;
    }
    
	/*
	 * single
	 */
	public static DbConnectionManager getInstance() {
		if(dbPoolManager==null){
			synchronized (providerLock) {
				if(dbPoolManager==null){
					dbPoolManager = new DbConnectionManager();
				}
			}
		}
		return dbPoolManager;
	}
	
    /**
     * Returns a database connection from the currently active connection
     * provider.
     */
    public synchronized static Connection getConnection(String poolName) {
        Connection conn=null;
        
        System.out.println("poolName :" +poolName);
        
        DbConnectionPool dbConnectionPool = DbConf.getInstance().getDbConnectionPool(poolName);
        ConnectionProvider connectionProvider = getDbConnectionPool(dbConnectionPool.getPoolname());
        
        System.out.println("dbConnectionPool :" +dbConnectionPool);
        System.out.println("connectionProvider :" +connectionProvider);

        if (connectionProvider == null) {
            synchronized (providerLock) {
                if (connectionProvider == null) {
					String className = dbConnectionPool.getConnectionProvider();
			        System.out.println("className :" +className);

                    if (className != null) {
                        //Attempt to load the class.
                        try {
                            Class<?> conClass = Class.forName(className);
                            connectionProvider = (ConnectionProvider)conClass.newInstance();
                        }catch(Exception e) {
                            System.err.println("Warning: failed to create the " +
                                "connection provider specified by connection" +
                                "Provider.className. Using the default pool.");
                          connectionProvider = new DbConnectionDefaultPool();
                        }
                        connectionProvider.setDbConnectionPool(dbConnectionPool);
                        connectionProviders.add(connectionProvider);
                    }
                    connectionProvider.start();
                }
            }
        }
        
        conn = connectionProvider.getConnection();
        
        if (conn == null) {
            System.err.println("WARNING: DbConnectionManager.getConnection() " +
                "failed to obtain a connection.");
        }
        return conn;
    }
    
    public static Connection getConnection() {
    	return getConnection(DbConf.getInstance().getMydb().getDefaultPool());
    }
    
}
