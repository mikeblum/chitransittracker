package com.chitransittracker.jdbc;

import java.sql.Connection;
import java.sql.DriverManager;

import org.apache.http.client.utils.URIBuilder;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;

public class PGConnector implements DBConnector{
	Logger logger = Logger.getLogger(PGConnector.class);
	
	private ConnectionDetails connectionDetails;
	
	public PGConnector(ConnectionDetails connectionDetails){
		this.connectionDetails = connectionDetails;
	}
	
	@Override
	public Connection getDBConnection() {
		try {
			URIBuilder pgURI = new URIBuilder();
			pgURI.setScheme("jdbc:postgresql")
				 .setHost(connectionDetails.getHost())
				 .setPort(connectionDetails.getPort())
				 .setPath("/" + connectionDetails.getDbName());
			return DriverManager.getConnection(
					pgURI.build().toString(), connectionDetails.getUsername(), connectionDetails.getPassword()
				);
		} catch (Exception e) {
			logger.debug("Failed to get postgres db connection!", e);
		}
		return null;
	}
}
