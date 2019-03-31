package ftp_loader;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.SocketException;

import org.apache.commons.net.ftp.FTPClient;

public class FTP {
	String host, username, password;
	FTPClient client;
	
	public FTP(String host, String username, String password) {
		super();
		this.host = host;
		this.username = username;
		this.password = password;
		client = new FTPClient();
		connect();
		login();
	}
	
	// load file from ftp server to InputStream
	public InputStream getInputStreamFromFTP(String remoteFilePath) {
		InputStream in = null;
		try {
			in = client.retrieveFileStream(remoteFilePath);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return in;
	}
	
	public void saveFileToLocal(String remoteFilePath, String localPath) throws FileNotFoundException, IOException {
		client.retrieveFile(remoteFilePath, new FileOutputStream(localPath));
	}
	
	public void connect() {
		try {
			client.connect(host);
		} catch (SocketException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public boolean login() {
		try {
			return client.login(username, password);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	
	public boolean logout() {
		try {
			return client.logout();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}
}
