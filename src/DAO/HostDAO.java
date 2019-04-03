package DAO;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import model.Host;

public class HostDAO {

	/**
	 * load full host from db
	 */
	public static List<Host> loadHost(Connection cnn) throws SQLException {
		List<Host> result = new ArrayList<>();
		String query = "select * from host_config";
		Statement stmt = null;
		ResultSet rs = null;
		stmt = cnn.createStatement();
		rs = stmt.executeQuery(query);

		while (rs.next()) {
		result.add(new Host(rs.getInt(1), rs.getString(2), rs.getString(3), rs.getString(4), rs.getString(8), rs.getString(7), rs.getString(10), rs.getString(11), rs.getString(12)));
		}
		rs.close();
		stmt.close();
		return result;
	}
}
