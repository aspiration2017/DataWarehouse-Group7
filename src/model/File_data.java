package model;

public class File_data {
	public String id, fileName, dirPath, localPath, status;
	public Host host;

	public File_data(String id, String fileName, String dirPath, String localPath, String status, Host host) {
		super();
		this.id = id;
		this.fileName = fileName;
		this.dirPath = dirPath;
		this.localPath = localPath;
		this.status = status;
		this.host = host;
	}

	public File_data() {
		super();
	}

}
