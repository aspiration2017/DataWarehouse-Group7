package application;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import DAO.DataWarehouse;
import DAO.File_dataDAO;
import DAO.HostDAO;
import DAO.Staging;
import connection.ControlConnection;
import connection.StagingConnection;
import connection.WarehouseConnection;
import model.File_data;
import model.Host;

public class StagingToWarehouse {
	private static Connection cnnControl = new ControlConnection().getCnn();
	private static Connection cnnStaging = new StagingConnection().getCnn();
	private static Connection cnnWarehouse = new WarehouseConnection().getCnn();
	private static List<Host> hosts = HostDAO.loadHost(cnnControl);
	
	public void loadDataFromStagingIntoWarehouse() throws SQLException {
		List<File_data> files = File_dataDAO.loadedStagingFiles(cnnControl);
		Host host;
		cnnWarehouse.setAutoCommit(false);
		cnnStaging.setAutoCommit(false);
		cnnControl.setAutoCommit(false);
		for (File_data file : files) {
			file.setHost(hosts);
			host = file.host;
			try {
				List<String> datas = Staging.getData(cnnStaging, host);
				DataWarehouse.insertWarehouse(cnnWarehouse, host, datas); // insert into dw
				File_dataDAO.updateTimeLoadDataIntoWarehouse(cnnControl, file.id); // update time upload
				File_dataDAO.updateStatus(cnnControl, file.id, "inwarehouse"); // update stt
				Staging.truncateStagingTable(cnnStaging, host.des_table);
				cnnWarehouse.commit();
				cnnStaging.commit();
				cnnControl.commit();
				System.out.println("loaded into warehouse " + host.des_table);
			} catch (Exception e) {
				e.printStackTrace();
				cnnControl.rollback();
				cnnWarehouse.rollback();
				cnnStaging.rollback();
			}
		}
		cnnWarehouse.setAutoCommit(true);
		cnnStaging.setAutoCommit(true);
		cnnControl.setAutoCommit(true);
	}
	
	public void closeControlCnn() throws SQLException {
		cnnControl.close();
	}

	public void closeStagingCnn() throws SQLException {
		cnnStaging.close();
	}

	public void closeWarehouseCnn() throws SQLException {
		cnnWarehouse.close();
	}
	
	public static void main(String[] args) throws SQLException {
		StagingToWarehouse s = new StagingToWarehouse();
		s.loadDataFromStagingIntoWarehouse();
		s.closeControlCnn();
		s.closeStagingCnn();
		s.closeWarehouseCnn();
	}
}
