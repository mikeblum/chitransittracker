package com.chitransittracker.jdbc;

import java.net.MalformedURLException;
import java.net.URL;

import org.apache.http.client.utils.URIBuilder;
import org.apache.log4j.Logger;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

public class SolrConnector implements DBConnector{
	private URL solrUrl;
	
	Logger logger = Logger.getLogger(SolrConnector.class);
	
	private static DateTimeFormatter solrDateFormatter = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:sss'Z'");
	
	private ConnectionDetails connectionDetails;
	
	public SolrConnector(ConnectionDetails connectionDetails){
		this.connectionDetails = connectionDetails;
	}
	
	@Override
	public Object getDBConnection() {
		SolrClient solrServer;
		try {
			URIBuilder solrURI = new URIBuilder();
			solrURI.setScheme("http")
				 .setHost(connectionDetails.getHost())
				 .setPort(connectionDetails.getPort())
				 .setPath("/solr/" + connectionDetails.getDbName());
			solrUrl = solrURI.build().toURL();
		} catch (Exception urlEx) {
			logger.debug("Failed to build solr url: " + solrUrl, urlEx);
		}
		solrServer = new HttpSolrClient(solrUrl.toString());
		
		try {
			solrServer.ping();
		} catch (Exception e) {
			logger.debug("Failed to connect to " + solrUrl, e);
		}
		
		return solrServer;
	}
	
	public static DateTimeFormatter getSolrDateTimeFormat(){
		return solrDateFormatter;
	}
	
}
