package application;

import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.util.Map;
import java.util.Map.Entry;

import DAO.DataMart;
import DAO.DataWarehouse;
import connection.DataMartConnection;
import connection.WarehouseConnection;

public class WarehouseToDatamart {
	private static Connection cnnWarehouse = new WarehouseConnection().getCnn();
	private static Connection cnnMart = new DataMartConnection().getCnn();
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
	
	public void closeWarehouseCnn() throws SQLException {
		cnnWarehouse.close();
	}

	public void closeMartCnn() throws SQLException {
		cnnMart.close();
	}
	public static void main(String[] args) throws SQLException {
		WarehouseToDatamart w = new WarehouseToDatamart();
		w.updateStatistic();
		w.closeMartCnn();
		w.closeWarehouseCnn();
	}
}
