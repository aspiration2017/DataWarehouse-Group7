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
	 * load file with status = ready. (file just insert to file_data_log)
	 */
	public static List<File_data> loadFilesNotDownloadYet(Connection cnn) throws SQLException {
		List<File_data> result = new ArrayList<>();
		String sql = "select * from file_data_log where status = 'ready'";
		Statement stmt = cnn.createStatement();
		ResultSet rs = stmt.executeQuery(sql);
		while (rs.next()) {
			result.add(new File_data(rs.getInt(1), rs.getInt(2), rs.getString(3), rs.getInt(5), rs.getInt(6), rs.getString(4)));
		}
		rs.close();
		stmt.close();
		return result;
	}

	/**
	 * load file with status = downloaded (file available in local)
	 */
	public static List<File_data> loadDownloadedFiles(Connection cnn) throws SQLException {
		List<File_data> result = new ArrayList<>();
		String sql = "select * from file_data_log where status = 'downloaded'";
		Statement stmt = cnn.createStatement();
		ResultSet rs = stmt.executeQuery(sql);
		while (rs.next()) {
			result.add(new File_data(rs.getInt(1), rs.getInt(2), rs.getString(3), rs.getInt(5), rs.getInt(6), rs.getString(4)));
		}
		rs.close();
		stmt.close();
		return result;
	}
	
	public static List<File_data> loadedStagingFiles(Connection cnn) throws SQLException {
		List<File_data> result = new ArrayList<>();
		String sql = "select id, id_host from file_data_log where status = 'instaging'";
		Statement stmt = cnn.createStatement();
		ResultSet rs = stmt.executeQuery(sql);
		while (rs.next()) {
			result.add(new File_data(rs.getInt(1), rs.getInt(2)));
		}
		rs.close();
		stmt.close();
		return result;
	}

	/**
	 * update status file when download, error,...etc
	 */
	public static void updateStatus(Connection cnn, int idFile, String stt) throws SQLException {
		String sql = "UPDATE file_data_log SET status = ? where id = ?";
		PreparedStatement stmt = cnn.prepareStatement(sql);
		stmt.setString(1, stt);
		stmt.setInt(2, idFile);
		stmt.executeUpdate();
		stmt.close();
	}

	public static void updateTimeDownload(Connection cnn, int idFile) throws SQLException {
		String sql = "UPDATE file_data_log SET time_download = NOW() where id = ?";
		PreparedStatement stmt = cnn.prepareStatement(sql);
		stmt.setInt(1, idFile);
		stmt.executeUpdate();
		stmt.close();
	}

	public static void updateTimeLoadIntoStaging(Connection cnn, int idFile) throws SQLException {
		String sql = "UPDATE file_data_log SET time_load_staging = NOW() where id = ?";
		PreparedStatement stmt = cnn.prepareStatement(sql);
		stmt.setInt(1, idFile);
		stmt.executeUpdate();
		stmt.close();
	}
	
	public static void updateTimeLoadDataIntoWarehouse(Connection cnn, int idFile) throws SQLException {
		String sql = "UPDATE file_data_log SET time_load_dw = NOW() where id = ?";
		PreparedStatement stmt = cnn.prepareStatement(sql);
		stmt.setInt(1, idFile);
		stmt.executeUpdate();
		stmt.close();
	}

	/**
	 * count loaded rows and insert it into db control.
	 */
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
	 */
	public static int loadFileFromLocalToStaging(Connection cnn, File_data file) throws IOException, SQLException {
		int loaded_rows = 0;
		BufferedReader reader = new BufferedReader(
				new InputStreamReader(new FileInputStream(file.host.file_localPath + file.fileName), "utf-8"));
		int numberOfFields = file.getNumberOfFields();
		StringBuilder value = new StringBuilder();
		for (int i = 0; i < numberOfFields; i++) {
			if (i == numberOfFields - 1)
				value.append("?");
			else
				value.append("?,");
		}
		StringTokenizer st;
		String sql = "INSERT INTO " + file.host.des_table + file.host.fields + " VALUES(" + value.toString() + ")";
		PreparedStatement stmt;
		stmt = cnn.prepareStatement(sql);
		String line = "";
		boolean error = false;
		while ((line = reader.readLine()) != null) {
			System.out.println(line);
			try {
				st = new StringTokenizer(line, file.host.delimiter);
				for (int i = 1; i <= numberOfFields; i++) {
					if(st.hasMoreTokens())
						stmt.setString(i, st.nextToken());
					else {
						error = true;
						i = Integer.MAX_VALUE-1;
					}
				}
				if (!error) {
					stmt.executeUpdate();
					loaded_rows++;
				}
				error = false;
			} catch (SQLException e) {
				System.out.println(e.getMessage());
			}
		}
		stmt.close();
		reader.close();
		return loaded_rows;
	}
	
}
