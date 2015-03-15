package com.chitransittracker.solr.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.joda.time.ReadableInstant;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import com.chitransittracker.jdbc.ConnectionDetails;
import com.chitransittracker.jdbc.SolrConnector;

public class SolrQueryCTAStops {
	
	static Logger logger = Logger.getLogger(SolrQueryCTAStops.class);
	//env variables for holding db credentials
	private static final String SOLR_HOST_NAME 	= "SOLR_HOST_NAME";
	private static final String SOLR_PORT 		= "SOLR_PORT";
	private static final String CTA_STOPS 		= "cta_stops";
	
	public static DateTimeFormatter DB_DATE = DateTimeFormat.forPattern("MM/dd/yyyy HH:mm:ss");
	
	static ConnectionDetails injectedDetails = new ConnectionDetails()
												.setHost(System.getenv(SOLR_HOST_NAME))
												.setPort(Integer.parseInt(System.getenv(SOLR_PORT)))
												.setDbName(CTA_STOPS);
	
	public static List<Map<String, String>> getNearbyStops(String latLon){
		SolrConnector solrConnector = new SolrConnector(injectedDetails);
		SolrClient solrServer = (SolrClient) solrConnector.getDBConnection();
		List<Map<String, String>> searchResults = new ArrayList<Map<String, String>>();
		SolrQuery query = new SolrQuery();
		//wt=json&sort=score%20asc&q={!geofilt%20score=distance%20sfield=location%20pt=41.952595,-87.648426%20d=.2}
		query.setRequestHandler("/select")
			 .setSort("score", SolrQuery.ORDER.asc)
			 .setQuery("{!geofilt score=distance sfield=location pt=" + latLon +  " d=.2}");
		logger.info(query.getQuery());
		try {
			ObjectMapper mapper = new ObjectMapper();
			TypeReference typeRef = new TypeReference<HashMap<String,String>>(){};
			
			QueryResponse qResponse = solrServer.query(query);
			Iterator<SolrDocument> resultsItr = qResponse.getResults().iterator();
			while(resultsItr.hasNext()){
				SolrDocument result = resultsItr.next();
				logger.debug(result.getFieldValueMap().toString());
				//convert Map to JSON ready map
				Iterator<String> keyItr = result.getFieldValueMap().keySet().iterator();
				Map<String, String> jsonMap = new HashMap<String, String>();
				while(keyItr.hasNext()){
					String key = keyItr.next();
					String value;
					if(result.getFieldValueMap().get(key) instanceof java.util.Date){
						value = DB_DATE.print(((java.util.Date) result.getFieldValueMap().get(key)).getTime());
					}else if(result.getFieldValueMap().get(key) instanceof Long){
						value = Long.toString((Long) result.getFieldValueMap().get(key));
					}else if(result.getFieldValueMap().get(key) instanceof Integer){
						value = Integer.toString((Integer) result.getFieldValueMap().get(key));
					}else{
						value = (String) result.getFieldValueMap().get(key);
					}
					jsonMap.put(key, value);
				}

				searchResults.add(jsonMap);
			}
		} catch (SolrServerException e) {
			logger.error("Failed to get nearby CTA stops.", e);
		}
		return searchResults;
	}
}
