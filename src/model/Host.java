package model;

public class Host {
	public int id, number_of_fields;
	public String hostName, username, password, delimiter, fields, des_table, file_dir, file_localPath, select_staging_to_dw,
			dw_fields_insert, format_fields, format_date, dw_values_insert;

	public Host(int id, String hostName, String username, String password, String fields, String delimiter,
			String des_table, String file_dir, String file_localPath, String select_staging_to_dw, String dw_query_insert, String dw_values_insert,
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
		this.select_staging_to_dw = select_staging_to_dw;
		this.dw_fields_insert = dw_query_insert;
		this.dw_values_insert = dw_values_insert;
		this.number_of_fields = number_of_fields;
		this.format_fields = format_fields;
		this.format_date = format_date;
	}

	public Host() {
		super();
	}

	@Override
	public String toString() {
		return id + " " + hostName + " " + username + " " + password + " " + fields + " " + delimiter + " " + des_table+" "+select_staging_to_dw+" "+dw_fields_insert+" "+number_of_fields+" "+format_fields+" "+format_date;
	}

}
