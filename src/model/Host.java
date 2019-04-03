package model;

public class Host {
	public int id;
	public String hostName, username, password, delimiter, fields, des_table, file_dir, file_localPath;

	public Host(int id, String hostName, String username, String password, String delimiter, String fields,
			String des_table, String file_dir, String file_localPath) {
		super();
		this.id = id;
		this.hostName = hostName;
		this.username = username;
		this.password = password;
		this.delimiter = delimiter;
		this.fields = fields;
		this.des_table = des_table;
		this.file_dir = file_dir;
		this.file_localPath = file_localPath;
	}

	public Host() {
		super();
	}

	@Override
	public String toString() {
		return id + " " + hostName + " " + username + " " + password + " " + fields + " " + delimiter + " " + des_table;
	}

}
