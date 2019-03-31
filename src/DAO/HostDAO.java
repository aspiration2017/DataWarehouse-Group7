package DAO;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import model.Host;

public class HostDAO {
	
	public List<Host> loadHost(Connection cnn) {
		List<Host> result = new ArrayList<>();
		// TODO load host from db control (table host_config)
		
		return result;
	}
}
