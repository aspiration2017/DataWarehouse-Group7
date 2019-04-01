package DAO;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import model.File_data;

public class File_dataDAO {

	/**
	 * load file with status = 0
	 * 
	 * @param cnn
	 * @return
	 * @throws SQLException
	 */
	public static List<File_data> loadFileNotDownloadYet(Connection cnn) throws SQLException {
		List<File_data> result = new ArrayList<>();
		String sql = "select * from file_data_log where status = 0";
		Statement stmt = cnn.createStatement();
		ResultSet rs = stmt.executeQuery(sql);
		while (rs.next()) {
			result.add(new File_data(rs.getInt(1), rs.getInt(2), rs.getString(3), rs.getString(4), rs.getString(5),
					rs.getInt(6), rs.getInt(8)));
		}
		rs.close();
		stmt.close();
		return result;
	}

	/**
	 * load file with status = 1
	 * 
	 * @param cnn
	 * @return
	 * @throws SQLException
	 */
	public static List<File_data> loadDownloadedFile(Connection cnn) throws SQLException {
		List<File_data> result = new ArrayList<>();
		String sql = "select * from file_data_log where status = 1";
		Statement stmt = cnn.createStatement();
		ResultSet rs = stmt.executeQuery(sql);
		while (rs.next()) {
			result.add(new File_data(rs.getInt(1), rs.getInt(2), rs.getString(3), rs.getString(4), rs.getString(5),
					rs.getInt(6), rs.getInt(8)));
		}
		rs.close();
		stmt.close();
		return result;
	}

	/**
	 * update status file when download || extract file into staging
	 * 
	 * @param cnn
	 * @param idFile
	 * @param stt
	 * @throws SQLException
	 */
	public static void updateStatus(Connection cnn, int idFile, int stt) throws SQLException {
		String sql = "UPDATE file_data_log SET status = ? where id = ?";
		PreparedStatement stmt = cnn.prepareStatement(sql);
		stmt.setInt(1, stt);
		stmt.setInt(2, idFile);
		stmt.executeUpdate();
		stmt.close();
	}

	public static void updateLocalPathFile(Connection cnn, int idFile, String path) throws SQLException {
		String sql = "UPDATE file_data_log SET local_path = ? where id = ?";
		PreparedStatement stmt = cnn.prepareStatement(sql);
		stmt.setString(1, path);
		stmt.setInt(2, idFile);
		stmt.executeUpdate();
		stmt.close();
	}

	public static void updateLoadedRows(Connection cnn, int idFile, int loaded_rows) throws SQLException {
		String sql = "UPDATE file_data_log SET loaded_rows = ? where id = ?";
		PreparedStatement stmt = cnn.prepareStatement(sql);
		stmt.setInt(1, loaded_rows);
		stmt.setInt(2, idFile);
		stmt.executeUpdate();
		stmt.close();
	}
	
	/**
	 * load file from local to staging database
	 * @param cnn
	 * @param file
	 * @return number of loaded rows
	 * @throws IOException
	 */
	public static int loadFileFromLocalToStaging(Connection cnn, File_data file) throws IOException, SQLException {
		int loaded_rows = 0;
		BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file.localPath), "utf-8"));
		int numberOfFields = file.getNumberOfFields();
		StringBuilder value = new StringBuilder();
		for (int i = 0; i < numberOfFields; i++) {
			if (i == numberOfFields-1)
				value.append("?");
			else
				value.append("?,");
		}
		StringTokenizer st;
		String sql = "INSERT INTO "+ file.host.des_table+file.host.fields +" VALUES("+value.toString()+")";
		PreparedStatement stmt = cnn.prepareStatement(sql);
		String line = "";
		while ((line = reader.readLine()) != null) {
			st = new StringTokenizer(line, file.host.delimiter);
			for (int i = 1; i <= numberOfFields; i++) {
				stmt.setString(i, st.nextToken());
			}
			stmt.executeUpdate();
			loaded_rows++;
		}
		stmt.close();
		reader.close();
		return loaded_rows;
	}
}
