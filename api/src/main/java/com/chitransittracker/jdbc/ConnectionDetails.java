package com.chitransittracker.jdbc;

import org.springframework.beans.factory.annotation.Value;

public class ConnectionDetails {
	@Value("${db.pg.host}")
	private String host;
	
	@Value("${db.pg.port}")
	private int port;
	
	@Value("${db.pg.username}")
	private String username;
	
	@Value("${db.pg.password}")
	private String password;
	
	@Value("${db.pg.dbName}")
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
