package application;

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

public class LoadDataToStaging {
	private static Connection cnnControl = new ControlConnection().getCnn();
	private static Connection cnnStaging = new StagingConnection().getCnn();
	private static List<Host> hosts = HostDAO.loadHost(cnnControl);

	public void loadFileToStaging() throws SQLException {
		List<File_data> files = File_dataDAO.loadDownloadedFiles(cnnControl);
		int count = 0; // line loaded
		for (File_data file : files) {
			file.setHost(hosts);
			try {
				count = File_dataDAO.loadFileFromLocalToStaging(cnnStaging, file);
			} catch (IOException e) {
				System.out.println(e.getMessage());
				File_dataDAO.updateStatus(cnnControl, file.id, "errorstaging");
			} catch (SQLException e) {
				System.out.println(e.getMessage());
			}
			File_dataDAO.updateLoadedRows(cnnControl, file.id, count);
			File_dataDAO.updateTimeLoadIntoStaging(cnnControl, file.id);
			File_dataDAO.updateStatus(cnnControl, file.id, "instaging");

			System.out.println("loaded to staging: " + file.fileName);
		}
	}
	
	public void closeControlCnn() throws SQLException {
		cnnControl.close();
	}

	public void closeStagingCnn() throws SQLException {
		cnnStaging.close();
	}
	
	public static void main(String[] args) throws SQLException {
		LoadDataToStaging l = new LoadDataToStaging();
		l.loadFileToStaging();
		l.closeControlCnn();
		l.closeStagingCnn();
	}
}
