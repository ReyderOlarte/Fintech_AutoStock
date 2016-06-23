package com.autoStock.database;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.autoStock.Co;
import com.autoStock.internal.Config;
import com.autoStock.tools.Benchmark;
import com.jolbox.bonecp.BoneCP;
import com.jolbox.bonecp.BoneCPConfig;

/**
 * @author Kevin Kowalewski
 * 
 */
public class DatabaseTest {
	BoneCP connectionPool;
	Connection connection;

	public void test() throws SQLException {

		try {
			Class.forName("com.mysql.jdbc.Driver");
		} catch (ClassNotFoundException e1) {
			e1.printStackTrace();
		}

		BoneCPConfig config = new BoneCPConfig();
		config.setJdbcUrl("jdbc:mysql://" + Config.dbHost + ":" + Config.dbPort + "/" + Config.dbDatabase );
		config.setUsername(Config.dbUsername);
		config.setPassword(Config.dbPassword);
		config.setMinConnectionsPerPartition(1);
		config.setMaxConnectionsPerPartition(15);
		config.setPartitionCount(1);

		try {
			connectionPool = new BoneCP(config);
			Co.println("Database conected!");
		} catch (Exception e) {
		}

		Benchmark bench = new Benchmark();
		
		connection = connectionPool.getConnection();
		
		for (int i=0; i<100; i++){	
			bench.tick();
			Statement statement = connection.createStatement();
			ResultSet rs = statement.executeQuery("SELECT RAND()");
			//while (rs.next()) {
				rs.next();
				Co.println(rs.getString(1));
			//}
		}
		
		connection.close();
		
		bench.printTotal();
	}
}
