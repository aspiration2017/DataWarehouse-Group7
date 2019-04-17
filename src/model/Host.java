package model;

public class Host {
	public int id, number_of_fields;
	public String hostName, username, password, delimiter, fields, des_table, file_dir, file_localPath, dw_query_select,
			dw_query_insert, format_fields, format_date;

	public Host(int id, String hostName, String username, String password, String fields, String delimiter,
			String des_table, String file_dir, String file_localPath, String dw_query_select, String dw_query_insert,
			int number_of_fields, String format_fields, String format_date) {
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
		this.dw_query_select = dw_query_select;
		this.dw_query_insert = dw_query_insert;
		this.number_of_fields = number_of_fields;
		this.format_fields = format_fields;
		this.format_date = format_date;
	}

	public Host() {
		super();
	}

	@Override
	public String toString() {
		return id + " " + hostName + " " + username + " " + password + " " + fields + " " + delimiter + " " + des_table+" "+dw_query_select+" "+dw_query_insert+" "+number_of_fields+" "+format_fields+" "+format_date;
	}

}
