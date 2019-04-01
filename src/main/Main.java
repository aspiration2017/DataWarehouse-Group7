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
	
	public void downloadFile() throws SQLException {
		List<File_data> files = File_dataDAO.loadFileNotDownloadYet(cnnControl);
		for (File_data file : files) {
			file.setHost(hosts);
			try {
				file.saveToLocal();
//				try {
//					cnnControl.setAutoCommit(false);
//					File_dataDAO.updateLocalPathFile(cnnControl, file.id, file.localPath);
//					File_dataDAO.updateStatus(cnnControl, file.id, 1);
//					cnnControl.commit();
//					cnnControl.setAutoCommit(true);
//				} catch (Exception e) {
//					cnnControl.rollback();
//					e.printStackTrace();
//				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void loadFileToStaging() throws SQLException {
		List<File_data> files = File_dataDAO.loadDownloadedFile(cnnControl);
		int count = 0;
		for (File_data file : files) {
			try {
				count = File_dataDAO.loadFileFromLocalToStaging(cnnStaging, file);
			} catch (IOException e) {
				File_dataDAO.updateLoadedRows(cnnControl, file.id, count);
				e.printStackTrace();
			}
			File_dataDAO.updateLoadedRows(cnnControl, file.id, count);
		}
	}

	public static void main(String[] args) throws SQLException {
		Main m = new Main();
//		m.downloadFile();
//		m.loadFileToStaging();
		
		m.closeControlCnn();
		m.closeStagingCnn();
	}
}
