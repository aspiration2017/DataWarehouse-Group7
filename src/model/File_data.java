package model;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.StringTokenizer;

import ftp_loader.FTP;

public class File_data {
	public int id, idHost;
	public String fileName;
	public int loaded_rows;
	public String status;
	public Host host;


	public File_data(int id, int idHost, String fileName, int loaded_rows, String status) {
		super();
		this.id = id;
		this.idHost = idHost;
		this.fileName = fileName;
		this.loaded_rows = loaded_rows;
		this.status = status;
	}

	public File_data() {
		super();
	}
	
	/**
	 * set file's host from available hosts.
	 * @param lst
	 */
	public void setHost(List<Host> lst) {
		for (Host h : lst) {
			if (h.id == idHost) {
				host = h;
				return;
			}
		}
		System.out.println("not found host");
	}
	
	/**
	 * if FTP connect successful
	 * save file down to local with specific path.
	 * @return
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public boolean saveToLocal() throws FileNotFoundException, IOException {
		FTP ftp = new FTP(host.hostName, host.username, host.password);
		boolean connectFTP = ftp.connect();
		if (connectFTP) {
			boolean success = ftp.saveFileToLocal(host.file_dir+fileName, host.file_localPath+fileName);
			ftp.logout();
			ftp.disconnect();
			System.out.println("logged out host: " + host.hostName);
			if (success) {
				System.out.println("saved file " + id);
				return true;
			}
			else {
				System.out.println("fail file:" + id);
				return false;
			}
		}
		return false;
	}
	
	/**
	 * count number of file's field.
	 * @return
	 */
	public int getNumberOfFields() {
		return new StringTokenizer(host.fields, ",").countTokens();
	}
	
	@Override
	public String toString() {
		return id+" "+idHost+" "+fileName+" "+host.file_dir+" "+host.file_localPath+" "+status+" "+loaded_rows;
	}
}
