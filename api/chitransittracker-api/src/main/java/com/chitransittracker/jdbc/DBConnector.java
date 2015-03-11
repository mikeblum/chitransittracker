package com.chitransittracker.jdbc;

import java.sql.Connection;

public interface DBConnector {
	public Connection getDBConnection();
}