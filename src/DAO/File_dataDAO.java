package DAO;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import model.File_data;

public class File_dataDAO {
	Connection cnn;
	
	public File_dataDAO(Connection cnn) {
		super();
		this.cnn = cnn;
	}

	public List<File_data> loadFileNotDownloadYet() {
		List<File_data> result = new ArrayList<>();
		// TODO load files from db control (table file_data_log) with status : 0

		return result;
	}
	
	public List<File_data> loadDownloadedFile() {
		List<File_data> result = new ArrayList<>();
		// TODO load files from db control (table file_data_log) with status : 1

		return result;
	}
	
	public void updateStatus(String idFile, String stt) {
		// TODO update status file
	}
	
	public void updateLocalPathFile(String id, String path) {
		// TODO update file's localpath
	}
	
	public void updateExtractedRows(String line) {
		// TODO count number of row was loaded into staging, update it into table: file_data_log
	}
	
}
