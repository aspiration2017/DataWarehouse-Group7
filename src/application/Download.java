package application;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import DAO.File_dataDAO;
import DAO.HostDAO;
import connection.ControlConnection;
import model.File_data;
import model.Host;

public class Download {
	private static Connection cnnControl = new ControlConnection().getCnn();
	private static List<Host> hosts = HostDAO.loadHost(cnnControl);
	
	public void downloadFile() throws SQLException {
		List<File_data> files = File_dataDAO.loadFilesNotDownloadYet(cnnControl);
		for (File_data file : files) {
			file.setHost(hosts);
			try {
				boolean isSuccess = file.saveToLocal();
				if (isSuccess) {
					File_dataDAO.updateStatus(cnnControl, file.id, "downloaded");
					File_dataDAO.updateTimeDownload(cnnControl, file.id);
				} else
					File_dataDAO.updateStatus(cnnControl, file.id, "errordownload");
			} catch (IOException e) {
				System.out.println(e.getMessage());
			}
		}
	}
	
	public void closeControlCnn() throws SQLException {
		cnnControl.close();
	}
	
	public static void main(String[] args) {
		Download d = new Download();
		try {
			d.downloadFile();
			d.closeControlCnn();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
