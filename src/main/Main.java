package main;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import DAO.File_dataDAO;
import DAO.HostDAO;
import connection.ControlConnection;
import connection.StagingConnection;
import model.File_data;
import model.Host;

public class Main {
	private static List<Host> hosts;
	private static Connection cnnControl = new ControlConnection().getCnn();
	private static Connection cnnStaging = new StagingConnection().getCnn();
	
	static {
		try {
			hosts = HostDAO.loadHost(cnnControl);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void closeControlCnn() throws SQLException {
		cnnControl.close();
	}
	
	public void closeStagingCnn() throws SQLException {
		cnnStaging.close();
	}
	
	/**
	 * get files's info which not downloaded yet from db & download it.
	 * update status file_data_log from 'ready' to 'download' & update time downloaded
	 * @throws SQLException
	 */
	public void downloadFile() throws SQLException {
		List<File_data> files = File_dataDAO.loadFileNotDownloadYet(cnnControl);
		for (File_data file : files) {
			file.setHost(hosts);
			try {
				boolean isSuccess = file.saveToLocal();
				if (isSuccess) {
					File_dataDAO.updateStatus(cnnControl, file.id, "downloaded");
					File_dataDAO.updateTimeDownload(cnnControl, file.id);
				}
				else
					File_dataDAO.updateStatus(cnnControl, file.id, "error");
			} catch (IOException e) {
				System.out.println(e.getMessage());
			}
		}
	}
	
	/**
	 * get file's info which be downloaded.
	 * read file and INSERT file's data into db (staging) with compatible table
	 * update loaded rows & time loaded into db (control) file_data_log table
	 * @throws SQLException
	 */
	public void loadFileToStaging() throws SQLException {
		List<File_data> files = File_dataDAO.loadDownloadedFile(cnnControl);
		int count = 0;
		for (File_data file : files) {
			file.setHost(hosts);
			try {
				count = File_dataDAO.loadFileFromLocalToStaging(cnnStaging, file);
			} catch (IOException e) {
				e.printStackTrace();
			}
			File_dataDAO.updateLoadedRows(cnnControl, file.id, count);
			File_dataDAO.updateTimeLoadIntoStaging(cnnControl, file.id);
		}
	}

	public static void main(String[] args) throws SQLException {
		Main m = new Main();
		m.downloadFile();
		System.out.println("download end!");
		m.loadFileToStaging();
		System.out.println("load into staging end!");
		m.closeControlCnn();
		m.closeStagingCnn();
	}
}
