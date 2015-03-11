package com.chitransittracker;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.joda.time.DateTime;

import com.chitransittracker.jdbc.ConnectionDetails;
import com.chitransittracker.jdbc.PGConnector;
import com.cta.model.CTAAlert;
import com.cta.model.CTAAlerts;
import com.cta.util.CTAXmlParser;

public class Driver {
	static Logger logger = Logger.getLogger(Driver.class);
	
	static ConnectionDetails injectedDetails;
	static CTAXmlParser ctaParser;
	
	//env variables for holding db credentials
	private static final String POSTGRES_HOST_NAME 	= "POSTGRES_HOST_NAME";
	private static final String POSTGRES_PORT 		= "POSTGRES_PORT";
	private static final String POSTGRES_USER_NAME 	= "POSTGRES_USER_NAME";
	private static final String POSTGRES_PASSWORD 	= "POSTGRES_PASSWORD";
	private static final String POSTGRES_DATABASE 	= "POSTGRES_DATABASE";
	
	//key is the column name and the value is the type - order is important
	static LinkedHashMap<String, String> alert_columns = new LinkedHashMap<String, String>();
	
	static String addModiedTrigger = "CREATE OR REPLACE FUNCTION update_modified_column()" +	
			"RETURNS TRIGGER AS $$ BEGIN " +
			    "NEW.last_modified = now();" +
			    "RETURN NEW;" +	
			"END; $$ language 'plpgsql';";
	
	public static void main(String[] args) throws InterruptedException{
		long start_time = System.currentTimeMillis();
		
		//build map of columns and their respective types
		alert_columns.put("id", "serial"); //<-- unique identifier
		alert_columns.put("alert_id", "text");
		alert_columns.put("headline", "text");
		alert_columns.put("short_desc", "text");
		alert_columns.put("full_desc", "text");
		alert_columns.put("severity_score", "integer");
		alert_columns.put("severity_color", "text");
		alert_columns.put("severity_css", "text");
		alert_columns.put("impact", "text");
		alert_columns.put("event_start", "timestamp");
		alert_columns.put("event_end", "timestamp");
		alert_columns.put("tbd", "text");
		alert_columns.put("major_alert", "text");
		alert_columns.put("alert_url", "text");
		alert_columns.put("service_id", "text");
		alert_columns.put("last_modified", "timestamp");
		injectedDetails = new ConnectionDetails().setHost(System.getenv(POSTGRES_HOST_NAME))
												 .setPort(Integer.parseInt(System.getenv(POSTGRES_PORT)))
												 .setUsername(System.getenv(POSTGRES_USER_NAME))
												 .setPassword(System.getenv(POSTGRES_PASSWORD))
												 .setDbName(System.getenv(POSTGRES_DATABASE));
		ctaParser = new CTAXmlParser();
		logger.debug("Indexing CTA Alerts...");
		Driver.indexCTAAlerts();
		long end_time = System.currentTimeMillis();
		long total_time = (end_time - start_time);
		logger.debug("Indexing took " + String.format("%d min, %d sec", 
				TimeUnit.MILLISECONDS.toMinutes(total_time),
			    TimeUnit.MILLISECONDS.toSeconds(total_time) - 
			    TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(total_time))
		));
	}
	
	private static void indexCTAAlerts(){
		Connection pgConnection = new PGConnector(injectedDetails).getDBConnection();
	
		try {
			logger.debug(pgConnection.getMetaData().getURL());
			CTAAlerts ctaAlerts = ctaParser.getCTAAlerts();
			logger.debug("CTA Alerts: " + ctaAlerts.getAlerts().size());
			//Create generic routes table
			Statement createCTALinesTable = pgConnection.createStatement();
			//generate comma seperated listing of column name column type, 
			List<String> colValPairs = new ArrayList(alert_columns.keySet().size());
			Iterator<String> columnNamesItr = alert_columns.keySet().iterator();
			while(columnNamesItr.hasNext()){
				String columnName = columnNamesItr.next();
				colValPairs.add(columnName + " " + alert_columns.get(columnName));
			}
			String createRoutesTable = "CREATE TABLE IF NOT EXISTS cta_alerts(" + StringUtils.join(colValPairs, ", ") + ");";
			createCTALinesTable.execute(createRoutesTable);
			logger.debug("CTA Alerts table created!");
			
			//clean up old alerts
			Statement deleteOldAlerts = pgConnection.createStatement();
			int deletedRows = deleteOldAlerts.executeUpdate("DELETE FROM cta_alerts;");
			logger.debug("DELETE rows from cta_alerts: " + deletedRows);
			
			//add a modifed column to track stale records
			Statement modifedTrigger = pgConnection.createStatement();
			
			//deploy trigger and apply to the cta_rail_lines table
			boolean triggerAdded = modifedTrigger.execute(addModiedTrigger);
			logger.debug("last_modified trigger cta_alerts: " + triggerAdded);
			//drop old trigger
			pgConnection.createStatement().execute("DROP TRIGGER IF EXISTS update_route_modtime ON cta_alerts;");
			
			Statement applyTrigger = pgConnection.createStatement();
			String applyTriggerQuery = "CREATE TRIGGER update_route_modtime BEFORE UPDATE ON cta_alerts FOR EACH ROW EXECUTE PROCEDURE  update_modified_column();";
			boolean triggerAddedToTable = applyTrigger.execute(applyTriggerQuery);
			logger.debug("Tigger deployed for last_modifed: " + triggerAddedToTable);
			
			String insertQuery = "INSERT INTO cta_alerts (alert_id, headline, short_desc, full_desc, severity_score, severity_color, severity_css, impact, event_start, event_end, tbd, major_alert, alert_url, service_id) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";
			PreparedStatement insertCTAAlerts = pgConnection.prepareStatement(insertQuery);
			//create a table for all CTA Rail Lines
			Iterator<CTAAlert> alertsItr = ctaAlerts.getAlerts().iterator();
			int totalRows = 0;
			while(alertsItr.hasNext()){
				CTAAlert ctaAlert = alertsItr.next();
				int index = 1; //prepared statements index starts at 1
				Iterator<String> colItr = alert_columns.keySet().iterator();
				colItr.next(); //pass the unique id column
				for(Object attribute : ctaAlert.getAttributes()){
					String type = alert_columns.get(colItr.next());
					if(StringUtils.equalsIgnoreCase(type, "integer")){
						if(attribute == null){
							insertCTAAlerts.setNull(index++, Types.INTEGER);
						}else{
							insertCTAAlerts.setInt(index++, (Integer) attribute);
						}
					}else if(StringUtils.equalsIgnoreCase(type, "timestamp")){
						if(attribute == null){
							insertCTAAlerts.setNull(index++, Types.DATE);
						}else{
							java.sql.Date sqlDate = new java.sql.Date(((DateTime) attribute).toDate().getTime());
							insertCTAAlerts.setDate(index++, sqlDate); 
						}
					}else{
						if(attribute == null){
							insertCTAAlerts.setNull(index++, Types.VARCHAR);
						}else{
							//cast as a string
							insertCTAAlerts.setString(index++, (String) attribute);
						}
					}
				}
				totalRows += insertCTAAlerts.executeUpdate();
			}
			logger.debug("INSERT CTA ALERTS: " + insertQuery);
			logger.debug("inserted " + totalRows + " CTA Alerts into pg");
		} catch (SQLException e) {
			logger.debug("Failed to insert CTA alerts", e);
		}
	}
}