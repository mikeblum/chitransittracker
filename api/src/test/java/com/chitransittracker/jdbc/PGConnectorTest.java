package com.chitransittracker.jdbc;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.junit.Assert.fail;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.junit.Test;

public class PGConnectorTest {
	@Test
	public void testPsqlDatabaseConnection(){
		Connection testConnection = new PGConnector().setHost("localhost")
													 .setPort(5432)
													 .setUsername("postgres")
													 .setPassword("postgres")
													 .setDbName("chitransittracker")
													 .getDBConnection();
		try {
			Statement simpleStatement = testConnection.createStatement();
			boolean simpleResults = simpleStatement.execute("SELECT 1");
			assertThat(simpleResults).isTrue();
		} catch (SQLException e) {
			fail("Failed to connect to local postgres db: " + e.getMessage());
		}
	}
}
