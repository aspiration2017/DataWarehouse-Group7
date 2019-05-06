package DAO;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import model.Host;

public class Staging {
	public static void truncateStagingTable(Connection cnnStaging, String table_name) throws SQLException {
		String sql = "truncate table " + table_name;
		Statement stmt = cnnStaging.createStatement();
		stmt.executeUpdate(sql);
		stmt.close();
	}

	public static List<String> getData(Connection cnnStaging, Host host) throws SQLException {
		List<String> result = new ArrayList<>();
		String sql = "select " + host.dw_query_select + " from " + host.des_table;
		Statement stmt = cnnStaging.createStatement();
		ResultSet rs = stmt.executeQuery(sql);
		StringBuilder sb = new StringBuilder();
		while (rs.next()) {
			sb.delete(0, sb.length());
			for (int i = 1; i <= host.number_of_fields; i++) {
				if (i == host.number_of_fields)
					sb.append(rs.getString(i));
				else
					sb.append(rs.getString(i).concat(","));
			}
			result.add(sb.toString());
		}
		return result;
	}
}
