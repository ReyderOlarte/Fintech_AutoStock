package com.autoStock.database;

import java.sql.Connection;

import com.autoStock.Co;
import com.autoStock.internal.Config;
import com.jolbox.bonecp.BoneCP;
import com.jolbox.bonecp.BoneCPConfig;

/**
 * @author Kevin Kowalewski
 *
 */
public class DatabaseCore {
	
	static BoneCP connectionPool;
	Connection connection;
	
	public void init(){
		try {
			Class.forName("com.mysql.jdbc.Driver");
		} catch (ClassNotFoundException e) { }

		BoneCPConfig config = new BoneCPConfig();
		config.setJdbcUrl("jdbc:mysql://"+Config.dbHost+":"+Config.dbPort+"/" + Config.dbDatabase);
		config.setUsername(Config.dbUsername);
		config.setPassword(Config.dbPassword);
		config.setMinConnectionsPerPartition(Config.dbMinConnectionsPerParition);
		config.setMaxConnectionsPerPartition(Config.dbMaxConnectionsPerParition);
		config.setPartitionCount(Config.dbConnectionPartitions);
		
		try{
			connectionPool = new BoneCP(config);
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public static Connection getConnection(){
		try {
			return connectionPool.getConnection();
		}catch(Exception e){
			e.printStackTrace();
			return null;
		}
	}
}
