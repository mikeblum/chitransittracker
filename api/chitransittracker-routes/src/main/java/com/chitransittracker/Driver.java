package com.chitransittracker;

import java.util.Iterator;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.common.SolrInputDocument;
import org.joda.time.DateTime;

import com.chitransittracker.jdbc.ConnectionDetails;
import com.chitransittracker.jdbc.SolrConnector;
import com.cta.bus.model.CTABusDirections;
import com.cta.bus.model.CTABusRoute;
import com.cta.bus.model.CTABusRoutes;
import com.cta.bus.model.CTABusStop;
import com.cta.bus.model.CTABusStops;
import com.cta.model.CTARoute;
import com.cta.model.CTARoutes;
import com.cta.util.CTAUtil;
import com.cta.util.CTAXmlParser;

public class Driver {
	static Logger logger = Logger.getLogger(Driver.class);
	
	static CTAXmlParser ctaParser;
	
	//env variables for holding db credentials
	private static final String SOLR_HOST_NAME 	= "SOLR_HOST_NAME";
	private static final String SOLR_PORT 		= "SOLR_PORT";
	
	static ConnectionDetails injectedDetails = new ConnectionDetails()
												.setHost(System.getenv(SOLR_HOST_NAME))
												.setPort(Integer.parseInt(System.getenv(SOLR_PORT)));
	
	private static String[] attributeNames = {
			"route_name",
			"address",
			"route_color",
			"route_text_color",
			"service_id",
			"route_url",
			"route_status",
			"route_status_color",
			"type"
		};
	
	private static String[] busStopAttributeNames = {
		"stop_id",
		"stop_number",
		"location",
		"service_id",
		"route_name",
		"route_color",
		"direction",
		"type"
	};
	
	private static String TYPE_RAIL = "RAIL";
	private static String TYPE_BUS = "BUS";
	
	public static void main(String[] args) throws InterruptedException{
		long start_time = System.currentTimeMillis();

		ctaParser = new CTAXmlParser();
		logger.debug("Indexing CTA Rail Lines...");
		//index the CTA Rail Lines
		Driver.indexCTARailLines();
		Thread.sleep(1000);
		logger.debug("Indexing CTA Train Stations...");
		//index CTA Rail Stations
		Driver.indexCTARailStations();
		Thread.sleep(1000);
		logger.debug("Indexing CTA Bus Routes...");
		//index CTA Bus Routes
		Driver.indexCTABusRoutes();
		Thread.sleep(1000);
		//index CTA Bus Stops
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
		CTARoutes ctaRailLines = ctaParser.getCTARoutesInfo(CTAUtil.RAIL);
		logger.debug("CTA Rail Lines: " + ctaRailLines.getRoutes().size());
		try {
			injectedDetails.setDbName("cta_rail_lines");
			SolrConnector solrConnector = new SolrConnector(injectedDetails);
			SolrClient solrServer = (SolrClient) solrConnector.getDBConnection();
			//clean index
			solrServer.deleteByQuery("*:*");
			Iterator<CTARoute> ctaRouteItr = ctaRailLines.getRoutes().iterator();
			while(ctaRouteItr.hasNext()){
				CTARoute ctaRoute = ctaRouteItr.next();
				Object[] attributes = ctaRoute.getAttributes();
				SolrInputDocument solrDoc = new SolrInputDocument();
				solrDoc.addField( "id", UUID.randomUUID());
				for(int index = 0; index < attributes.length; index++){
					//if its a date Solr expects a certain format
					if(attributes[index] instanceof DateTime){
						solrDoc.addField(attributeNames[index], SolrConnector.getSolrDateTimeFormat().print((DateTime) attributes[index]));
					}else{
						solrDoc.addField(attributeNames[index], attributes[index]);
					}
				}
				solrDoc.addField("last_modified", SolrConnector.getSolrDateTimeFormat().print(new DateTime()) );
			    solrServer.add(solrDoc);
			}
			solrServer.commit();
			logger.debug("inserted " + ctaRailLines.getRoutes().size() + " CTA Rail Lines into solr");
		} catch (Exception e) {
			logger.debug("Failed to insert CTA rail lines", e);
		}
	}
	
	private static void indexCTARailStations(){
		CTARoutes ctaRailStations = ctaParser.getCTARoutesInfo(CTAUtil.STATION);
		logger.debug("CTA Stations: " + ctaRailStations.getRoutes().size());
		try {
			injectedDetails.setDbName("cta_routes");
			SolrConnector solrConnector = new SolrConnector(injectedDetails);
			SolrClient solrServer = (SolrClient) solrConnector.getDBConnection();
			//clean index
			solrServer.deleteByQuery("*:*");
			Iterator<CTARoute> ctaRouteItr = ctaRailStations.getRoutes().iterator();
			while(ctaRouteItr.hasNext()){
				CTARoute ctaRoute = ctaRouteItr.next();
				ctaRoute.setType(TYPE_RAIL);
				Object[] attributes = ctaRoute.getAttributes();
				SolrInputDocument solrDoc = new SolrInputDocument();
				solrDoc.addField( "id", UUID.randomUUID());
				for(int index = 0; index < attributes.length; index++){
					//if its a date Solr expects a certain format
					if(attributes[index] instanceof DateTime){
						solrDoc.addField(attributeNames[index], SolrConnector.getSolrDateTimeFormat().print((DateTime) attributes[index]));
					}else{
						solrDoc.addField(attributeNames[index], attributes[index]);
					}
				}
				solrDoc.addField("last_modified", SolrConnector.getSolrDateTimeFormat().print(new DateTime()) );
			    solrServer.add(solrDoc);
			}
			solrServer.commit();
			logger.debug("inserted " + ctaRailStations.getRoutes().size() + " CTA Rail Stations into solr");
		} catch (Exception e) {
			logger.debug("Failed to insert CTA rail stations", e);
		}
	}
	
	private static void indexCTABusRoutes(){
		CTARoutes ctaBusRoutes = ctaParser.getCTARoutesInfo(CTAUtil.BUS);
		logger.debug("CTA Bus Lines: " + ctaBusRoutes.getRoutes().size());
		try {
			injectedDetails.setDbName("cta_routes");
			SolrConnector solrConnector = new SolrConnector(injectedDetails);
			SolrClient solrServer = (SolrClient) solrConnector.getDBConnection();
			//dont' clean the index, rail stations already cleaned up
			Iterator<CTARoute> ctaRouteItr = ctaBusRoutes.getRoutes().iterator();
			while(ctaRouteItr.hasNext()){
				CTARoute ctaRoute = ctaRouteItr.next();
				ctaRoute.setType(TYPE_BUS);
				Object[] attributes = ctaRoute.getAttributes();
				SolrInputDocument solrDoc = new SolrInputDocument();
				solrDoc.addField( "id", UUID.randomUUID());
				for(int index = 0; index < attributes.length; index++){
					//if its a date Solr expects a certain format
					if(attributes[index] instanceof DateTime){
						solrDoc.addField(attributeNames[index], SolrConnector.getSolrDateTimeFormat().print((DateTime) attributes[index]));
					}else{
						solrDoc.addField(attributeNames[index], attributes[index]);
					}
				}
				solrDoc.addField("last_modified", SolrConnector.getSolrDateTimeFormat().print(new DateTime()) );
			    solrServer.add(solrDoc);
			}
			solrServer.commit();
			logger.debug("inserted " + ctaBusRoutes.getRoutes().size() + " CTA Bus Routes into solr");
		} catch (Exception e) {
			logger.debug("Failed to insert CTA bus stops", e);
		}
	}
	
	private static void indexCTABusStops(){
		CTABusRoutes ctaBusRoutes = ctaParser.getBusRoutes();
		logger.debug("CTA Bus Lines: " + ctaBusRoutes.getBusRoutes().size());
		try {
			injectedDetails.setDbName("cta_routes");
			SolrConnector solrConnector = new SolrConnector(injectedDetails);
			SolrClient solrServer = (SolrClient) solrConnector.getDBConnection();
			//don't clean the index, rail stations already cleaned up
			Iterator<CTABusRoute> ctaBusRouteItr = ctaBusRoutes.getBusRoutes().iterator();
			int numBusStops = 0;
			while(ctaBusRouteItr.hasNext()){
				CTABusRoute ctaRoute = ctaBusRouteItr.next();
				logger.debug("Getting cardinal directions for: " + ctaRoute.getRouteNumber());
				//get all cardinal directions for this route
				CTABusDirections cardinalDirections = CTAXmlParser.getBusRouteDirections(ctaRoute.getRouteNumber());
				//get stops for this direction
				for(String direction: cardinalDirections.getDirections()){
					logger.debug("Processing " + ctaRoute.getRouteNumber() + " direction: " + direction);
					CTABusStops busStops = CTAXmlParser.getBusStops(ctaRoute.getRouteNumber(), direction);
					for(CTABusStop busStop: busStops.getBusStops()){
						//build up bus stop record that references the bus route metadata
						busStop.setType(TYPE_BUS);
						busStop.setDirection(direction);
						busStop.setRouteName(ctaRoute.getRouteName());
						busStop.setRouteNumber(ctaRoute.getRouteNumber());
						busStop.setRouteColor(ctaRoute.getRouteColor());
						Object[] attributes = busStop.getAttributes();
						SolrInputDocument solrDoc = new SolrInputDocument();
						solrDoc.addField( "id", UUID.randomUUID());
						for(int index = 0; index < attributes.length; index++){
							//if its a date Solr expects a certain format
							if(attributes[index] instanceof DateTime){
								solrDoc.addField(busStopAttributeNames[index], SolrConnector.getSolrDateTimeFormat().print((DateTime) attributes[index]));
							}else{
								solrDoc.addField(busStopAttributeNames[index], attributes[index]);
							}
						}
						solrDoc.addField("last_modified", SolrConnector.getSolrDateTimeFormat().print(new DateTime()) );
					    solrServer.add(solrDoc);
					    numBusStops++;
					}
				}
			}
			solrServer.commit();
			logger.debug("inserted " + numBusStops + " CTA Bus Stops into solr");
		} catch (Exception e) {
			logger.debug("Failed to insert CTA bus stops", e);
		}
	}
}