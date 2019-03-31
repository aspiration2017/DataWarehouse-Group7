package model;

public class Host {
	public String id, hostName, username, password;

	public Host(String id, String hostName, String username, String password) {
		super();
		this.id = id;
		this.hostName = hostName;
		this.username = username;
		this.password = password;
	}

	public Host() {
		super();
	}

}
