package connection;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DataMartConnection {
	private Connection cnn;

	public DataMartConnection() {
		super();
		String host = "cakkun.mysql.database.azure.com";
		String dbName = "datamart";
		String username = "dwg7@cakkun";
		String password = "D@tawarehouse2019";
		String url = String.format("jdbc:mysql://%s/%s", host, dbName);
		Properties properties = new Properties();
		properties.setProperty("user", username);
		properties.setProperty("password", password);
		properties.setProperty("useSSL", "true");
		properties.setProperty("verifyServerCertificate", "true");
		properties.setProperty("requireSSL", "false");
		properties.setProperty("zeroDateTimeBehavior", "CONVERT_TO_NULL");
		properties.setProperty("serverTimezone", "UTC");
		try {
			Class.forName("com.mysql.cj.jdbc.Driver"); // new driver for connector/J 8.0.15
			cnn = DriverManager.getConnection(url, properties);
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	public Connection getCnn() {
		return cnn;
	}
}
