package ftp_loader;

import java.io.BufferedReader;
import java.io.IOException;
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
	}
	
	public BufferedReader download(String remoteFilePath) {
		BufferedReader reader = null;
		// TODO Auto-generated method stub
		
		return reader;
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
