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
	
	@Autowired
	static ConnectionDetails injectedDetails;
	static CTAXmlParser ctaParser;
	
	static String route_columns = "route_name text,route_color text,route_text_color text,service_id text,route_url text,route_status text,route_status_color text,last_modified timestamp default now()";
	
	static String addModiedTrigger = "CREATE OR REPLACE FUNCTION update_modified_column()" +	
			"RETURNS TRIGGER AS $$ BEGIN " +
			    "NEW.last_modified = now();" +
			    "RETURN NEW;" +	
			"END; $$ language 'plpgsql';";
	
	public static void main(String[] args){
		long start_time = System.currentTimeMillis();
		//stand up Spring Context
		ApplicationContext context = new ClassPathXmlApplicationContext("applicationContext.xml");
		injectedDetails = (ConnectionDetails) context.getBean("connectionDetails");
		ctaParser = new CTAXmlParser();
		logger.debug("Indexing CTA Rail Lines...");
		//index the CTA Rail Lines
		Driver.indexCTARailLines();
		logger.debug("Indexing CTA Train Stations...");
		//index CTA Rail Stations
		Driver.indexCTARailStations();
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
			String createRoutesTable = "CREATE TABLE IF NOT EXISTS cta_rail_stations(" + route_columns + ");";
			createCTALinesTable.execute(createRoutesTable);
			logger.debug("CTA Rail Stations table created!");
			
			//clean up old routes
			Statement deleteOldRoutes = pgConnection.createStatement();
			int deletedRows = deleteOldRoutes.executeUpdate("DELETE FROM cta_rail_stations;");
			logger.debug("DELETE rows from cta_rail_stations: " + deletedRows);
			
			//add a modifed column to track stale records
			Statement modifedTrigger = pgConnection.createStatement();
			
			//deploy trigger and apply to the cta_rail_lines table
			boolean triggerAdded = modifedTrigger.execute(addModiedTrigger);
			logger.debug("last_modified trigger cta_rail_stations: " + triggerAdded);
			//drop old trigger
			pgConnection.createStatement().execute("DROP TRIGGER IF EXISTS update_route_modtime ON cta_rail_stations;");
			
			Statement applyTrigger = pgConnection.createStatement();
			String applyTriggerQuery = "CREATE TRIGGER update_route_modtime BEFORE UPDATE ON cta_rail_stations FOR EACH ROW EXECUTE PROCEDURE  update_modified_column();";
			boolean triggerAddedToTable = applyTrigger.execute(applyTriggerQuery);
			logger.debug("Tigger deployed for last_modifed: " + triggerAddedToTable);
			
			String insertQuery = "INSERT INTO cta_rail_stations (route_name, route_color, route_text_color, service_id, route_url, route_status, route_status_color) VALUES (?, ?, ?, ?, ?, ?, ?);";
			PreparedStatement insertCTARailStations = pgConnection.prepareStatement(insertQuery);
			//create a table for all CTA Rail Lines
			Iterator<CTARoute> railStationsItr = ctaRailStations.getRoutes().iterator();
			int totalRows = 0;
			while(railStationsItr.hasNext()){
				CTARoute railStation = railStationsItr.next();
				int index = 1; //prepared statements index starts at 1
				for(String attribute : railStation.getAttributes()){
					insertCTARailStations.setString(index++, attribute);
				}
				totalRows += insertCTARailStations.executeUpdate();
				
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
			String createBusRoutesTable = "CREATE TABLE IF NOT EXISTS cta_bus_stops(" + route_columns + ");";
			createCTABusRoutesTable.execute(createBusRoutesTable);
			logger.debug("CTA Bus Stops table created!");
			
			//clean up old routes
			Statement deleteOldRoutes = pgConnection.createStatement();
			int deletedRows = deleteOldRoutes.executeUpdate("DELETE FROM cta_bus_stops;");
			logger.debug("DELETE rows from cta_bus_stops: " + deletedRows);
			
			//add a modifed column to track stale records
			Statement modifedTrigger = pgConnection.createStatement();
			
			//deploy trigger and apply to the cta_rail_lines table
			boolean triggerAdded = modifedTrigger.execute(addModiedTrigger);
			logger.debug("last_modified trigger: " + triggerAdded);
			//drop old trigger
			pgConnection.createStatement().execute("DROP TRIGGER IF EXISTS update_route_modtime ON cta_bus_stops;");
			
			Statement applyTrigger = pgConnection.createStatement();
			String applyTriggerQuery = "CREATE TRIGGER update_route_modtime BEFORE UPDATE ON cta_bus_stops FOR EACH ROW EXECUTE PROCEDURE  update_modified_column();";
			boolean triggerAddedToTable = applyTrigger.execute(applyTriggerQuery);
			logger.debug("Tigger deployed for last_modifed cta_bus_stops: " + triggerAddedToTable);
			
			String insertQuery = "INSERT INTO cta_bus_stops (route_name, route_color, route_text_color, service_id, route_url, route_status, route_status_color) VALUES (?, ?, ?, ?, ?, ?, ?);";
			PreparedStatement insertCTARailStations = pgConnection.prepareStatement(insertQuery);
			//create a table for all CTA Bus Stops
			Iterator<CTARoute> busStopsItr = ctaBusStops.getRoutes().iterator();
			int totalRows = 0;
			while(busStopsItr.hasNext()){
				CTARoute railStation = busStopsItr.next();
				int index = 1; //prepared statements index starts at 1
				for(String attribute : railStation.getAttributes()){
					insertCTARailStations.setString(index++, attribute);
				}
				totalRows += insertCTARailStations.executeUpdate();
				
			}
			logger.debug("INSERT CTA BUS STOPS: " + insertQuery);
			logger.debug("inserted " + totalRows + " CTA Bus Stops into pg");
		} catch (SQLException e) {
			logger.debug("Failed to insert CTA bus stops", e);
		}
	}
}