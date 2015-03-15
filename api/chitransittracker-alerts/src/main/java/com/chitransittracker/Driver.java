package com.chitransittracker;

import java.util.Iterator;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.common.SolrInputDocument;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import com.chitransittracker.jdbc.ConnectionDetails;
import com.chitransittracker.jdbc.SolrConnector;
import com.cta.model.CTAAlert;
import com.cta.model.CTAAlerts;
import com.cta.util.CTAXmlParser;

public class Driver {
	static Logger logger = Logger.getLogger(Driver.class);
	
	static ConnectionDetails injectedDetails;
	static CTAXmlParser ctaParser;
	
	//env variables for holding db credentials
	private static final String SOLR_HOST_NAME 	= "SOLR_HOST_NAME";
	private static final String SOLR_PORT 		= "SOLR_PORT";
	private static final String SOLR_DATABASE 	= "cta_alerts";
	
	//build map of columns and their respective types
	private static String[] attributeNames = {
									   "alert_id",
									   "headline",
									   "short_desc",
									   "full_desc",
									   "severity_score",
									   "severity_color",
									   "severity_css",
									   "impact",
									   "event_start",
									   "event_end",
									   "tbd",
									   "major_alert",
									   "alert_url",
									   "service_id"
									  };
	
	
	public static void main(String[] args) throws InterruptedException{
		long start_time = System.currentTimeMillis();
		
		injectedDetails = new ConnectionDetails().setHost(System.getenv(SOLR_HOST_NAME))
												 .setPort(Integer.parseInt(System.getenv(SOLR_PORT)))
												 .setDbName(SOLR_DATABASE);

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
		try {
			CTAAlerts ctaAlerts = ctaParser.getCTAAlerts();
			logger.debug("CTA Alerts: " + ctaAlerts.getAlerts().size());
			SolrConnector solrConnector = new SolrConnector(injectedDetails);
			SolrClient solrServer = (SolrClient) solrConnector.getDBConnection();
			//clean index
			solrServer.deleteByQuery("*:*");
			Iterator<CTAAlert> ctaAlertItr = ctaAlerts.getAlerts().iterator();
			while(ctaAlertItr.hasNext()){
				CTAAlert ctaAlert = ctaAlertItr.next();
				Object[] attributes = ctaAlert.getAttributes();
				SolrInputDocument solrDoc = new SolrInputDocument();
				solrDoc.addField( "id", UUID.randomUUID());
				for(int index = 0; index < attributes.length; index++){
					//if its a date Solr expects a certain format
					if(attributes[index] instanceof DateTime){
						solrDoc.addField(attributeNames[index], solrDateFormatter.print((DateTime) attributes[index]));
					}else{
						solrDoc.addField(attributeNames[index], attributes[index]);
					}
				}
				solrDoc.addField("last_modified", solrDateFormatter.print(new DateTime()) );
			    solrServer.add(solrDoc);
			}
			solrServer.commit();
			logger.debug("inserted " + ctaAlerts.getAlerts().size() + " CTA Alerts into solr");
		} catch (Exception e) {
			logger.debug("Failed to insert CTA alerts", e);
		}
	}
}