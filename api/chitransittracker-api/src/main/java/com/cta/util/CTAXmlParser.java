package com.cta.util;

import java.io.InputStream;
import java.net.URI;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.log4j.Logger;

import com.cta.model.CTAAlerts;
import com.cta.model.CTAArrivals;
import com.cta.model.CTARoutes;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;

public class CTAXmlParser {
	static Logger logger = Logger.getLogger(CTAXmlParser.class);
	
	protected static RequestConfig requestConfig = RequestConfig.custom().setConnectTimeout(30 * 1000).build();
	protected static HttpClient client = HttpClientBuilder.create().setDefaultRequestConfig(requestConfig).build();
	
	public static void setHttpClient(HttpClient client){
		CTAXmlParser.client = client;
	}
	
	/**
	 * Given a type, deserialize CTA XML to POJOs
	 * @param type rail, bus, station, or systemwide
	 * @return deserialzed XML routes as POJOs
	 */
	public static CTARoutes getCTARoutesInfo(String type){
		try {
			URI uriToFetchData = CTAUtil.getCTARoutesURI(type).build();
			InputStream fetchedResultsStream = processAPIRequest(uriToFetchData);
			XmlMapper xmlMapper = new XmlMapper();
			xmlMapper.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);
			return xmlMapper.readValue(fetchedResultsStream, CTARoutes.class);
		} catch (Exception e) {
			logger.error("Failed to retireve CTA routes info for type: " + type, e);
		}
		return null;
	}
	
	/**
	 * deserialize CTA XML Alerts to POJOs
	 * @return deserialzed XML alerts as POJOs
	 */
	public static CTAAlerts getCTAAlerts(){
		try {
			URI uriToFetchData = CTAUtil.getCTAAlertsURI().build();
			InputStream fetchedResultsStream = processAPIRequest(uriToFetchData);
			XmlMapper xmlMapper = new XmlMapper();
			xmlMapper.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);
			return xmlMapper.readValue(fetchedResultsStream, CTAAlerts.class);
		} catch (Exception e) {
			logger.error("Failed to retireve CTA alerts", e);
		}
		return null;
	}
	
	public static CTAArrivals getCTAArrivals(String stationId, String maxArrivals){
		try {
			URI uriToFetchData = CTAUtil.getCTAArrivalsURI(stationId, maxArrivals).build();
			InputStream fetchedResultsStream = processAPIRequest(uriToFetchData);
			XmlMapper xmlMapper = new XmlMapper();
			xmlMapper.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);
			return xmlMapper.readValue(fetchedResultsStream, CTAArrivals.class);
		} catch (Exception e) {
			logger.error("Failed to retireve CTA arrivals for " + stationId, e);
		}
		return null;
	}
	
	/**
	 * Util method for making 
	 * @param type
	 * @return
	 */
	private static InputStream processAPIRequest(URI uri){
		URI uriToFetchData = uri;
		HttpGet request;
		try {
			request = new HttpGet(uriToFetchData);
			// add request header
			request.addHeader("User-Agent", CTAUtil.user_agent);
			HttpResponse responseTrainStations = client.execute(request);
			HttpEntity entity = responseTrainStations.getEntity(); 
			return entity.getContent();
		} catch (Exception e) {
			logger.debug("Failed to fetch data at: " + uriToFetchData.toString(), e);
		}
		return null;
	}
}
