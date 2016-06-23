package com.autoStock.internal;

/**
 * @author Kevin Kowalewski
 *
 */
public class Config {
	public static int comListenPort = 8888;
	public static int comSocketTimeout = 30;
	public static int dbPort = 3306;
	public static String dbHost = "192.168.8.132";
	public static String dbDatabase = "autoStock";
	public static int dbConnectionPartitions = 1;
	public static int dbMinConnectionsPerParition = 1;
	public static int dbMaxConnectionsPerParition = 8;
	public static String dbUsername = "autoStock";
	public static String dbPassword = "SSmxynk--autoStock";
	public static String plIbUsername = "wealth000";
	public static String plIbPassword = "Fishing2";
	public static int plIbTwsPort = 888;
	public static String plIbTwsHost = "192.168.8.132";
}
