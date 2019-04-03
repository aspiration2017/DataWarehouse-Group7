package ftp_loader;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.SocketException;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;

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
	
	/**
	 *  load file from FTP server to InputStream (not used)
	 */
	public InputStream getInputStreamFromFTP(String remoteFilePath) {
		InputStream in = null;
		try {
			in = client.retrieveFileStream(remoteFilePath);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return in;
	}
	
	/**
	 * save file to local from FTP with remoteFilePath in server & localPath 
	 */
	public boolean saveFileToLocal(String remoteFilePath, String localPath) throws FileNotFoundException, IOException {
		return client.retrieveFile(remoteFilePath, new FileOutputStream(new File(localPath)));
	}
	
	/**
	 * FTP connect to host.
	 * if FTP's reply is Positive, disconnect this FTP
	 * else Login into that FTP
	 */
	public boolean connect() {
		try {
			client.connect(host, 21);
			client.enterLocalPassiveMode();
			if (!FTPReply.isPositiveCompletion(client.getReplyCode())) {
				disconnect();
				return false;
			}
			else
				return login();
		} catch (SocketException e) {
			System.out.println(e.getMessage());
			System.out.println("host: " + host);
		} catch (IOException e) {
			System.out.println(e.getMessage());
			System.out.println("host: " + host);
		}
		return false;
	}
	
	public boolean login() {
		try {
			return client.login(username, password);
		} catch (Exception e) {
			System.out.println("error login: "+ host+" "+ e.getMessage());
		}
		return false;
	}
	
	public boolean logout() {
		try {
			client.logout();
			return true;
		} catch (IOException e) {
			System.out.println("error logout:" + host +" "+ e.getMessage());
		}
		return false;
	}
	
	public void disconnect() {
		try {
			client.disconnect();
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}
	}
}
