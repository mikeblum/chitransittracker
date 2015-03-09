package com.chitransittracker;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Iterator;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.chitransittracker.jdbc.ConnectionDetails;
import com.chitransittracker.jdbc.PGConnector;
import com.cta.model.CTARoute;
import com.cta.model.CTARoutes;
import com.cta.util.CTAUtil;
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
	
	static String route_columns = "route_name text,route_color text,route_text_color text,service_id text, route_url text,route_status text,route_status_color text, type text, last_modified timestamp default now()";
	
	static String addModiedTrigger = "CREATE OR REPLACE FUNCTION update_modified_column()" +	
			"RETURNS TRIGGER AS $$ BEGIN " +
			    "NEW.last_modified = now();" +
			    "RETURN NEW;" +	
			"END; $$ language 'plpgsql';";
	
	public static void main(String[] args) throws InterruptedException{
		long start_time = System.currentTimeMillis();
		injectedDetails = new ConnectionDetails().setHost(System.getenv(POSTGRES_HOST_NAME))
												 .setPort(Integer.parseInt(System.getenv(POSTGRES_PORT)))
												 .setUsername(System.getenv(POSTGRES_USER_NAME))
												 .setPassword(System.getenv(POSTGRES_PASSWORD))
												 .setDbName(System.getenv(POSTGRES_DATABASE));
		ctaParser = new CTAXmlParser();
		logger.debug("Indexing CTA Rail Lines...");
		//index the CTA Rail Lines
		Driver.indexCTARailLines();
		Thread.sleep(1000);
		logger.debug("Indexing CTA Train Stations...");
		//index CTA Rail Stations
		Driver.indexCTARailStations();
		Thread.sleep(1000);
		logger.debug("Indexing CTA Bus Stops...");
		//index CTA Bus Stations
		Driver.indexCTABusStops();
		logger.debug("Done indexing CTA Train Lines, Stations, and Bus Stops");
		long end_time = System.currentTimeMillis();
		long total_time = (end_time - start_time);
		logger.debug("Indexing took " + String.format("%d min, %d sec", 
				TimeUnit.MILLISECONDS.toMinutes(total_time),
			    TimeUnit.MILLISECONDS.toSeconds(total_time) - 
			    TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(total_time))
		));
	}
	
	private static void indexCTARailLines(){
		Connection pgConnection = new PGConnector(injectedDetails).getDBConnection();
		CTARoutes ctaRailLines = ctaParser.getCTARoutesInfo(CTAUtil.RAIL);
		logger.debug("CTA Rail Lines: " + ctaRailLines.getRoutes().size());
		try {
			//Create generic routes table
			Statement createCTALinesTable = pgConnection.createStatement();
			String createRoutesTable = "CREATE TABLE IF NOT EXISTS cta_rail_lines(" + route_columns + ");";
			createCTALinesTable.execute(createRoutesTable);
			logger.debug("CTA Rail Lines table created!");
			
			//clean up old routes
			Statement deleteOldRoutes = pgConnection.createStatement();
			int deletedRows = deleteOldRoutes.executeUpdate("DELETE FROM cta_rail_lines;");
			logger.debug("DELETE rows from cta_rail_lines: " + deletedRows);
			
			//add a modifed column to track stale records
			Statement modifedTrigger = pgConnection.createStatement();
			
			//deploy trigger and apply to the cta_rail_lines table
			boolean triggerAdded = modifedTrigger.execute(addModiedTrigger);
			logger.debug("last_modified trigger cta_rail_lines: " + triggerAdded);
			//drop old trigger
			pgConnection.createStatement().execute("DROP TRIGGER IF EXISTS update_route_modtime ON cta_rail_lines;");
			
			Statement applyTrigger = pgConnection.createStatement();
			String applyTriggerQuery = "CREATE TRIGGER update_route_modtime BEFORE UPDATE ON cta_rail_lines FOR EACH ROW EXECUTE PROCEDURE  update_modified_column();";
			boolean triggerAddedToTable = applyTrigger.execute(applyTriggerQuery);
			logger.debug("Tigger deployed for last_modifed: " + triggerAddedToTable);
			
			String insertQuery = "INSERT INTO cta_rail_lines (route_name, route_color, route_text_color, service_id, route_url, route_status, route_status_color) VALUES (?, ?, ?, ?, ?, ?, ?);";
			PreparedStatement insertCTARailLines = pgConnection.prepareStatement(insertQuery);
			//create a table for all CTA Rail Lines
			Iterator<CTARoute> railLinesItr = ctaRailLines.getRoutes().iterator();
			int totalRows = 0;
			while(railLinesItr.hasNext()){
				CTARoute railLine = railLinesItr.next();
				int index = 1; //prepared statements index starts at 1
				for(String attribute : railLine.getAttributes()){
					insertCTARailLines.setString(index++, attribute);
				}
				totalRows += insertCTARailLines.executeUpdate();
			}
			logger.debug("INSERT CTA LINES: " + insertQuery);
			logger.debug("inserted " + totalRows + " CTA Rail Lines into pg");
		} catch (SQLException e) {
			logger.debug("Failed to insert CTA rail lines", e);
		}
	}
	
	private static void indexCTARailStations(){
		Connection pgConnection = new PGConnector(injectedDetails).getDBConnection();
		CTARoutes ctaRailStations = ctaParser.getCTARoutesInfo(CTAUtil.STATION);
		logger.debug("CTA Stations: " + ctaRailStations.getRoutes().size());
		try {
			//Create generic routes table
			Statement createCTALinesTable = pgConnection.createStatement();
			String createRoutesTable = "CREATE TABLE IF NOT EXISTS cta_routes(" + route_columns + ");";
			createCTALinesTable.execute(createRoutesTable);
			logger.debug("CTA Rail Stations table created!");
			
			//clean up old routes
			Statement deleteOldRoutes = pgConnection.createStatement();
			int deletedRows = deleteOldRoutes.executeUpdate("DELETE FROM cta_routes;");
			logger.debug("DELETE rows from cta_routes: " + deletedRows);
			
			//add a modifed column to track stale records
			Statement modifedTrigger = pgConnection.createStatement();
			
			//deploy trigger and apply to the cta_rail_lines table
			boolean triggerAdded = modifedTrigger.execute(addModiedTrigger);
			logger.debug("last_modified trigger cta_routes: " + triggerAdded);
			//drop old trigger
			pgConnection.createStatement().execute("DROP TRIGGER IF EXISTS update_route_modtime ON cta_routes;");
			
			Statement applyTrigger = pgConnection.createStatement();
			String applyTriggerQuery = "CREATE TRIGGER update_route_modtime BEFORE UPDATE ON cta_routes FOR EACH ROW EXECUTE PROCEDURE  update_modified_column();";
			boolean triggerAddedToTable = applyTrigger.execute(applyTriggerQuery);
			logger.debug("Tigger deployed for last_modifed: " + triggerAddedToTable);
			
			String insertQuery = "INSERT INTO cta_routes (route_name, route_color, route_text_color, service_id, route_url, route_status, route_status_color, type) VALUES (?, ?, ?, ?, ?, ?, ?, ?);";
			PreparedStatement insertCTARailStations = pgConnection.prepareStatement(insertQuery);
			//create a table for all CTA Rail Lines
			Iterator<CTARoute> railStationsItr = ctaRailStations.getRoutes().iterator();
			int totalRows = 0;
			while(railStationsItr.hasNext()){
				int index = 1; //prepared statements index starts at 1
				CTARoute railStation = railStationsItr.next();
				for(String attribute : railStation.getAttributes()){
					insertCTARailStations.setString(index++, attribute);
				}
				//set type to bus
				insertCTARailStations.setString(index++, "RAIL");
				try{
					totalRows += insertCTARailStations.executeUpdate();	
				}catch(Exception e){
					logger.debug("Failed to insert row: " + totalRows, e);
				}
			}
			
			logger.debug("INSERT CTA STATIONS: " + insertQuery);
			logger.debug("inserted " + totalRows + " CTA Rail Stations into pg");
		} catch (SQLException e) {
			logger.debug("Failed to insert CTA rail stations", e);
		}
	}
	
	private static void indexCTABusStops(){
		Connection pgConnection = new PGConnector(injectedDetails).getDBConnection();
		CTARoutes ctaBusStops = ctaParser.getCTARoutesInfo(CTAUtil.BUS);
		logger.debug("CTA Bus Lines: " + ctaBusStops.getRoutes().size());
		try {
			//Create generic routes table
			Statement createCTABusRoutesTable = pgConnection.createStatement();
			String createBusRoutesTable = "CREATE TABLE IF NOT EXISTS cta_routes(" + route_columns + ");";
			createCTABusRoutesTable.execute(createBusRoutesTable);
			logger.debug("CTA Bus Stops table created!");
			
			//DO NOt CLEAR TABLE - RAIL already here
			
			//add a modifed column to track stale records
			Statement modifedTrigger = pgConnection.createStatement();
			
			//deploy trigger and apply to the cta_rail_lines table
			boolean triggerAdded = modifedTrigger.execute(addModiedTrigger);
			logger.debug("last_modified trigger: " + triggerAdded);
			//drop old trigger
			pgConnection.createStatement().execute("DROP TRIGGER IF EXISTS update_route_modtime ON cta_routes;");
			
			Statement applyTrigger = pgConnection.createStatement();
			String applyTriggerQuery = "CREATE TRIGGER update_route_modtime BEFORE UPDATE ON cta_routes FOR EACH ROW EXECUTE PROCEDURE  update_modified_column();";
			boolean triggerAddedToTable = applyTrigger.execute(applyTriggerQuery);
			logger.debug("Tigger deployed for last_modifed cta_routes: " + triggerAddedToTable);
			
			String insertQuery = "INSERT INTO cta_routes (route_name, route_color, route_text_color, service_id, route_url, route_status, route_status_color, type) VALUES (?, ?, ?, ?, ?, ?, ?, ?);";
			PreparedStatement insertCTABusStops = pgConnection.prepareStatement(insertQuery);
			//create a table for all CTA Bus Stops
			Iterator<CTARoute> busStopsItr = ctaBusStops.getRoutes().iterator();
			int totalRows = 0;
			while(busStopsItr.hasNext()){
				int index = 1; //prepared statements index starts at 1
				CTARoute railStation = busStopsItr.next();
				for(String attribute : railStation.getAttributes()){
					insertCTABusStops.setString(index++, attribute);
				}
				//set type to bus
				insertCTABusStops.setString(index++, "BUS");
				try{
					totalRows += insertCTABusStops.executeUpdate();	
				}catch(Exception e){
					logger.debug("Failed to insert row: " + totalRows, e);
				}
			}
			
			logger.debug("INSERT CTA BUS STOPS: " + insertQuery);
			logger.debug("inserted " + totalRows + " CTA Bus Stops into pg");
		} catch (SQLException e) {
			logger.debug("Failed to insert CTA bus stops", e);
		}
	}
}