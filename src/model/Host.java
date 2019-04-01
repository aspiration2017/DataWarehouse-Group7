package model;

public class Host {
	public String hostName, username, password, delimiter, fields, des_table;
	public int id;

	
	public Host(int id, String hostName, String username, String password, String fields, String delimiter,
			String des_table) {
		super();
		this.id = id;
		this.hostName = hostName;
		this.username = username;
		this.password = password;
		this.fields = fields;
		this.delimiter = delimiter;
		this.des_table = des_table;
	}


	public Host() {
		super();
	}
	
	@Override
	public String toString() {
		return id+" "+hostName+" "+username+" "+password+" "+fields+" "+delimiter+" "+des_table;
	}

}
