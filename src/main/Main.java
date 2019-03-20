package main;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

public class Main {

	public static void main(String[] args) {
		String host = "cakkun.mysql.database.azure.com";
		String dbName = "control";
		String username = "dwg7@cakkun";
		String password = "D@tawarehouse2019";
		String url = String.format("jdbc:mysql://%s/%s", host, dbName);
		Properties properties = new Properties();
		properties.setProperty("user", username);
		properties.setProperty("password", password);
		properties.setProperty("useSSL", "true");
		properties.setProperty("verifyServerCertificate", "true");
		properties.setProperty("requireSSL", "false");
		
		try {
			Class.forName("com.mysql.jdbc.Driver");
			Connection cnn = DriverManager.getConnection(url, properties);
			String query = "select * from host_config";
			Statement stmt = cnn.createStatement();
			ResultSet rs = stmt.executeQuery(query);
			while (rs.next()) {
				System.out
						.println(rs.getInt(1) + " " + rs.getString(2) + " " + rs.getString(3) + " " + rs.getString(4));
			}

		} catch (SQLException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

}
