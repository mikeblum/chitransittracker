package com.chitransittracker.jdbc;


public class ConnectionDetails {
	private String host;
	private int port;
	private String username;
	private String password;
	private String dbName;
	
	public String getHost() {
		return host;
	}

	public ConnectionDetails setHost(String host) {
		this.host = host;
		return this;
	}

	public int getPort() {
		return port;
	}

	public ConnectionDetails setPort(int port) {
		this.port = port;
		return this;
	}

	public String getUsername() {
		return username;
	}

	public ConnectionDetails setUsername(String username) {
		this.username = username;
		return this;
	}

	public String getPassword() {
		return password;
	}

	public ConnectionDetails setPassword(String password) {
		this.password = password;
		return this;
	}

	public String getDbName() {
		return dbName;
	}

	public ConnectionDetails setDbName(String dbName) {
		this.dbName = dbName;
		return this;
	}
}
