package com.chitransittracker.jdbc;

import java.sql.Connection;
import java.sql.DriverManager;

import org.apache.http.client.utils.URIBuilder;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;

public class PGConnector implements DBConnector{
	
	Logger logger = Logger.getLogger(PGConnector.class);
	
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
	
	@Override
	public Connection getDBConnection() {
		try {
			URIBuilder pgURI = new URIBuilder();
			pgURI.setScheme("jdbc:postgresql")
				 .setHost(this.host)
				 .setPort(this.port)
				 .setPath("/" + this.dbName);
			return DriverManager.getConnection(
					pgURI.build().toString(), this.username, this.password
				);
		} catch (Exception e) {
			logger.debug("Failed to get postgres db connection!", e);
		}
		return null;
	}

	public String getHost() {
		return host;
	}

	public PGConnector setHost(String host) {
		this.host = host;
		return this;
	}

	public int getPort() {
		return port;
	}

	public PGConnector setPort(int port) {
		this.port = port;
		return this;
	}

	public String getUsername() {
		return username;
	}

	public PGConnector setUsername(String username) {
		this.username = username;
		return this;
	}

	public String getPassword() {
		return password;
	}

	public PGConnector setPassword(String password) {
		this.password = password;
		return this;
	}

	public String getDbName() {
		return dbName;
	}

	public PGConnector setDbName(String dbName) {
		this.dbName = dbName;
		return this;
	}
}
