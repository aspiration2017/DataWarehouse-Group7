package DAO;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

public class DataMart {
	public static void insertStatistic(Connection cnnDM, Date date, int amount) throws SQLException {
		String sql = "insert into statistic(date,amount_student) values(?,?)";
		PreparedStatement stmt = cnnDM.prepareStatement(sql);
		stmt.setDate(1, date);
		stmt.setInt(2, amount);
		stmt.executeUpdate();
		stmt.close();
	}
	
	public static void truncateStatistic(Connection cnnDM) throws SQLException {
		String sql = "truncate table statistic";
		Statement stmt = cnnDM.createStatement();
		stmt.executeUpdate(sql);
		stmt.close();
	}
}
