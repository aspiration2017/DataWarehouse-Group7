package application;

import java.io.IOException;
import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.StringTokenizer;

import DAO.DataMart;
import DAO.DataWarehouse;
import DAO.File_dataDAO;
import DAO.HostDAO;
import DAO.Staging;
import connection.ControlConnection;
import connection.DataMartConnection;
import connection.StagingConnection;
import connection.WarehouseConnection;
import model.File_data;
import model.Host;

public class Main {
	private static Connection cnnControl = new ControlConnection().getCnn();
	private static Connection cnnStaging = new StagingConnection().getCnn();
	private static Connection cnnWarehouse = new WarehouseConnection().getCnn();
	private static Connection cnnMart = new DataMartConnection().getCnn();
	private static List<Host> hosts = HostDAO.loadHost(cnnControl);

	/** step 1 :
	 * get files's info which not downloaded yet from db & download it. if not
	 * error, update file_data_log's status from 'ready' to 'download' & update time
	 * downloaded else update file_data_log's status from 'ready' to 'error' (don't
	 * update time)
	 */
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

	/** step 2:
	 * get file's info which be downloaded. read file and INSERT file's data into db
	 * (staging) with compatible table update loaded rows & time loaded into db
	 * (control) file_data_log table
	 */
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

	/** step 3:
	 * get data from staging's table and insert it into warehouse, then truncate
	 * that table.
	 */
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

	/** step 4:
	 * select group by date, and count number of students on that date.
	 */
	public void updateStatistic() throws SQLException {
		Map<Date, Integer> map = DataWarehouse.getAmountStudentPerDay(cnnWarehouse);
		cnnMart.setAutoCommit(false);
		try {
			DataMart.truncateStatistic(cnnMart);
			for (Entry<Date, Integer> e : map.entrySet()) {
				DataMart.insertStatistic(cnnMart, e.getKey(), e.getValue());
			}
			cnnMart.commit();
		} catch (Exception e2) {
			System.out.println(e2.getMessage());
			cnnMart.rollback();
		}
		cnnMart.setAutoCommit(true);	
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

	public void closeMartCnn() throws SQLException {
		cnnMart.close();
	}

	public static void main(String[] args) throws SQLException {
		Main m = new Main();
		
		m.downloadFile();
		m.loadFileToStaging();
		m.loadDataFromStagingIntoWarehouse();
		m.updateStatistic();
		
		m.closeMartCnn();
		m.closeControlCnn();
		m.closeStagingCnn();
		m.closeWarehouseCnn();
	}
}
