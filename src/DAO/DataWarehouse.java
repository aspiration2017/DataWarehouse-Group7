package DAO;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import model.Host;

public class DataWarehouse {
	public static int isExistedData(Connection cnnDW, String mssv, int id_source) throws SQLException {
		String sql = "select id from student where mssv = ? and id_source = ? and is_active = 1";
		PreparedStatement stmt = cnnDW.prepareStatement(sql);
		stmt.setString(1, mssv);
		stmt.setInt(2, id_source);
		ResultSet rs = stmt.executeQuery();
		if (rs.next()) {
			return rs.getInt(1);
		}
		rs.close();
		stmt.close();
		return -1;
	}

	/**
	public void insertWarehouse(Connection cnnDW, Host host, String oneLine) throws SQLException, ParseException {
		StringBuilder sb = new StringBuilder();
		sb.append("insert into student(");
		sb.append(host.dw_query_insert);
		sb.append(") values(");
		for (int i = 0; i < host.number_of_fields; i++) {
			sb.append("?,");
		}
		sb.append("?)");
		String sql = sb.toString();
		PreparedStatement stmt = cnnDW.prepareStatement(sql);
		StringTokenizer format = new StringTokenizer(host.format_fields, ",");
		StringTokenizer values = new StringTokenizer(oneLine, ",");
		String type = "";
		int i = 1;
		while (format.countTokens() > 0) {
			type = format.nextToken();
			switch (type) {
			case "int":
				stmt.setInt(i, Integer.parseInt(values.nextToken()));
				break;
			case "date":
				stmt.setDate(i, new Date(new SimpleDateFormat(host.format_date).parse(values.nextToken()).getTime()));
				break;
			case "string":
				stmt.setString(i, values.nextToken());
				break;
			default:
				break;
			}
			i++;
		}
		stmt.setInt(i, host.id);
		stmt.executeUpdate();
		stmt.close();
	}
	*/

	public static void insertWarehouse(Connection cnnDW, Host host, List<String> datas) throws SQLException, ParseException {
		String sql = "insert into STUDENT("+host.dw_fields_insert+") values"+host.dw_values_insert;
		PreparedStatement stmt = cnnDW.prepareStatement(sql);
		// 1. create array format
		StringTokenizer format = new StringTokenizer(host.format_fields, ",");
		String[] formatArr = new String[format.countTokens()];
		for (int i = 0; i < formatArr.length; i++) {
			formatArr[i] = format.nextToken();
		}
		
		// 2. read each line in "datas", using 'batch' 
		for (int i = 0; i < datas.size(); i++) {
			int k = 1, idExistedData;
			if ((idExistedData = isExistedData(cnnDW, getMssv(datas.get(i)), host.id)) > 0) // kiem tra du lieu bi trung
				updateNonActice(cnnDW, idExistedData); // update nonactive
				
			StringTokenizer values = new StringTokenizer(datas.get(i), ",");
			for(int j = 0; j < formatArr.length; j++) {
				String type = formatArr[j];
				String value = values.nextToken();
				switch (type) {
				case "int":
					stmt.setInt(k, Integer.parseInt(value));
					break;
				case "date":
					stmt.setDate(k, new Date(new SimpleDateFormat(host.format_date).parse(value).getTime()));
					break;
				case "string":
					stmt.setString(k, value);
					break;
				default:
					break;
				}
				k++;
			}
			stmt.setInt(k, host.id);
			stmt.addBatch();
		}
		stmt.executeBatch();
		stmt.close();
	}
	
	public static void updateNonActice(Connection cnnDW, int id) throws SQLException {
		String sql = "update student set is_active = 0 where id = ?";
		PreparedStatement stmt = cnnDW.prepareStatement(sql);
		stmt.setInt(1, id);
		stmt.executeUpdate();
		stmt.close();
	}
	
	/**
	 * count added line/day
	 */
	public static Map<Date, Integer> getAmountStudentPerDay(Connection cnnDW) throws SQLException {
		Map<Date, Integer> result = new HashMap<>();
		String sql = "select DATE(added_date) dateonly, count(id) from student group by dateonly";
		Statement stmt = cnnDW.createStatement();
		ResultSet rs = stmt.executeQuery(sql);
		while (rs.next()) {
			result.put(rs.getDate(1), rs.getInt(2));
		}
		rs.close();
		stmt.close();
		return result;
	}
	
	// get mssv from one line data
	public static String getMssv(String data) {
		StringTokenizer st = new StringTokenizer(data, ",");
		return st.nextToken();
	}
}
