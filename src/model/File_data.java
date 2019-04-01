package model;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.StringTokenizer;

import ftp_loader.FTP;

public class File_data {
	public int id, idHost;
	public String fileName, dirPath, localPath;
	public int status, loaded_rows;
	public Host host;

	public File_data(int id, int idHost, String fileName, String dirPath, String localPath, int status,
			int loaded_rows) {
		super();
		this.id = id;
		this.idHost = idHost;
		this.fileName = fileName;
		this.dirPath = dirPath;
		this.localPath = localPath;
		this.status = status;
		this.loaded_rows = loaded_rows;
	}

	public File_data() {
		super();
	}
	
	public void setHost(List<Host> lst) {
		for (Host h : lst) {
			if (h.id == idHost) {
				host = h;
				return;
			}
		}
		System.out.println("not found host");
	}
	
	public void saveToLocal() throws FileNotFoundException, IOException {
		if (localPath == null)
			localPath = "D:\\FTP\\"+fileName;
		FTP ftp = new FTP(host.hostName, host.username, host.password);
		ftp.saveFileToLocal(dirPath+fileName, localPath);
		ftp.logout();
		System.out.println("saved file " + id);
	}
	
	public int getNumberOfFields() {
		return new StringTokenizer(host.fields, ",").countTokens();
	}
	
	@Override
	public String toString() {
		return id+" "+idHost+" "+fileName+" "+dirPath+" "+localPath+" "+status+" "+loaded_rows;
	}
}
