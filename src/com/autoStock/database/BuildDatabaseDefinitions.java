/**
 * 
 */
package com.autoStock.database;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.util.Date;

import com.autoStock.Co;

/**
 * @author Kevin Kowalewski
 *
 */
public class BuildDatabaseDefinitions {
	public boolean writeGeneratedJavaFiles(){
		try {
			Connection connection = DatabaseCore.getConnection();
			DatabaseMetaData metaData = connection.getMetaData();
			ResultSet resultSetForTables = metaData.getTables(null, null, "%", null);
			
			FileWriter writer = new FileWriter(new File("gen/com/autoStock/generated/basicDefinitions/TableDefinitions.java"));
			PrintWriter printWriter  = new PrintWriter(writer);
			
			printWriter.println("package com.autoStock.generated.basicDefinitions;\n");
			printWriter.println("import java.util.Date;");
			printWriter.println("import com.autoStock.types.basic.Time;");
			printWriter.println("import com.autoStock.trading.platform.ib.definitions.HistoricalDataDefinitions.Resolution;");
			printWriter.println("public class TableDefinitions {\n");
			
			while (resultSetForTables.next()){
				boolean lastCharIsS = resultSetForTables.getString(3).substring(resultSetForTables.getString(3).length()-1).equals("s");
				String classString = resultSetForTables.getString(3).substring(0,1).toUpperCase() + resultSetForTables.getString(3).substring(1,resultSetForTables.getString(3).length() - (lastCharIsS ? 1 : 0));
				
				Co.println("--> Generating class: " + classString);
				
				printWriter.println("\tpublic static class Db" + classString + " {");
				ResultSet resultSetForColumns = metaData.getColumns(null, null, resultSetForTables.getString(3), null);
				while (resultSetForColumns.next()){
					if (resultSetForColumns.getString(4).equals("timeOffset")){
						printWriter.println("\t\tpublic " + getStringType("TIME") + " " + resultSetForColumns.getString(4) + ";");
					}else if (resultSetForColumns.getString(4).equals("resolution")){
						printWriter.println("\t\tpublic " + "Resolution" + " " + resultSetForColumns.getString(4) + ";");
					}else{
						printWriter.println("\t\tpublic " + getStringType(resultSetForColumns.getString(6)) + " " + resultSetForColumns.getString(4) + ";");						
					}
				}
				printWriter.println("\t}\n");
				printWriter.println("\tpublic static Db"+ classString +" db"+ classString +" = new TableDefinitions.Db"+ classString +"();\n");
			}
			
			printWriter.println("}");
			
			printWriter.close();
			writer.close();
		}catch(Exception e){
			e.printStackTrace();
			return false;
		}
		
		return true;
	}
	
	public static String getStringType(String sqlType){
		if (sqlType.equals("FLOAT") || sqlType.equals("DOUBLE") || sqlType.equals("DECIMAL")){return "double";}
		if (sqlType.equals("INT")){return "int";}
		if (sqlType.equals("BIGINT")){return "long";}
		if (sqlType.equals("DATETIME") || sqlType.equals("DATE")){return "Date";}
		if (sqlType.equals("VARCHAR") || sqlType.equals("BLOB") || sqlType.equals("LONGBLOB")){return "String";}
		if (sqlType.equals("TIME")){return "Time";}
		if (sqlType.equals("BIT")){return "boolean";}
		else {throw new UnsatisfiedLinkError("No type matched " + sqlType);}
	}
	
	public static Object getJavaType(String sqlType){
		if (sqlType.equals("FLOAT")){return Double.class;}
		if (sqlType.equals("INT")){return Integer.class;}
		if (sqlType.equals("BIGINT")){return Long.class;}
		if (sqlType.equals("DATETIME")){return Date.class;}
		if (sqlType.equals("VARCHAR")){return String.class;}
		if (sqlType.equals("BIT")){return Boolean.class;}
		else {throw new UnsatisfiedLinkError();}
	}
}
