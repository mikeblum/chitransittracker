package com.cta.util;

import java.io.InputStream;
import java.net.URI;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.cta.model.CTARoutes;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;

@Component
public class CTAXmlParser {
	static Logger logger = Logger.getLogger(CTAXmlParser.class);
	
	protected static HttpClient client = HttpClientBuilder.create().build();
	
	@Value("${cta.user-agent}")
	protected static String USER_AGENT;
	
	@Value("${cta.api.rail}")
	protected static String RAIL;
	@Value("${cta.api.bus}")
	protected static String BUS;
	@Value("${cta.api.station}")
	protected static String STATION;
	
	@Value("${cta.api.uri.scheme}")
	protected static String SCHEME;
	@Value("cta.api.uri.base")
	protected static String BASE;
	@Value("cta.api.uri.version")
	protected static String VERSION;
	
	//cta aspx endpoints
	@Value("cta.api.uri.routes")
	protected static String ROUTES;
	
	public static CTARoutes getCTARoutesInfo(String type){
		try {
			InputStream fetchedResultsStream = processAPIRoutesRequest(type);
			XmlMapper xmlMapper = new XmlMapper();
			xmlMapper.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);
			return xmlMapper.readValue(fetchedResultsStream, CTARoutes.class);
		} catch (Exception e) {
			logger.error("Failed to retireve CTA routes info for type: " + type, e);
		}
		return null;
	}
	
	private static InputStream processAPIRoutesRequest(String type){
		URI uriToFetchData = null;
		HttpGet getTrainStations;
		try {
			uriToFetchData = CTAXmlParser.getCTARoutesURI(type).build();
			getTrainStations = new HttpGet(uriToFetchData);
			// add request header
			getTrainStations.addHeader("User-Agent", USER_AGENT);
			HttpResponse responseTrainStations = client.execute(getTrainStations);
			HttpEntity entity = responseTrainStations.getEntity(); 
			return entity.getContent();
		} catch (Exception e) {
			logger.debug("Failed to fetch data at: " + uriToFetchData.toString(), e);
		}
		return null;
	}
	
	/**
	 * Base CTA API url - expects the caller to add the API version, path, and any parameters
	 * @return spring-injected cta.api.uri.scheme + cta.api.uri.base
	 */
	public static URIBuilder getAPIBase(){
		try{
			return new URIBuilder()
			.setScheme(CTAXmlParser.SCHEME)
			.setHost(CTAXmlParser.BASE);
		}catch(Exception e){
			logger.debug("Failed to build uri for CTA api.", e);
		}
		return null;
	}
	
	public static URIBuilder getCTARoutesURI(String type){
		URIBuilder trainStationsURI = getAPIBase();
		trainStationsURI.setPath(CTAXmlParser.VERSION + "/" + CTAXmlParser.ROUTES)
					.setParameter("type", type);
		return trainStationsURI;
	}
}
