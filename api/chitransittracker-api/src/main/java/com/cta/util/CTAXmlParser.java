package com.cta.util;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.log4j.Logger;

import com.cta.bus.model.CTABusArrivals;
import com.cta.bus.model.CTABusDirections;
import com.cta.bus.model.CTABusRoutes;
import com.cta.bus.model.CTABusStops;
import com.cta.model.CTAAlerts;
import com.cta.model.CTAArrivals;
import com.cta.model.CTARoutes;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;

public class CTAXmlParser {
	static Logger logger = Logger.getLogger(CTAXmlParser.class);
	
	protected static RequestConfig requestConfig = RequestConfig.custom().setConnectTimeout(30 * 1000).build();
	
	public static HttpClient getHttpClient(){
		return HttpClientBuilder.create().setDefaultRequestConfig(requestConfig).build();
	}
	
	/**
	 * Given a type, deserialize CTA XML to POJOs
	 * @param type rail, bus, station, or systemwide
	 * @return deserialzed XML routes as POJOs
	 */
	public static CTARoutes getCTARoutesInfo(String type){
		try {
			URI uriToFetchData = CTAUtil.getCTARoutesURI(type).build();
			logger.debug(uriToFetchData.toString());
			InputStream fetchedResultsStream = CTAXmlParser.processAPIRequest(uriToFetchData);
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
	 * @throws IOException 
	 */
	public static CTAAlerts getCTAAlerts(){
		InputStream fetchedResultsStream = null;
		try {
			URI uriToFetchData = CTAUtil.getCTAAlertsURI().build();
			fetchedResultsStream = CTAXmlParser.processAPIRequest(uriToFetchData);
			XmlMapper xmlMapper = new XmlMapper();
			xmlMapper.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);
			return xmlMapper.readValue(fetchedResultsStream, CTAAlerts.class);
		} catch (Exception e) {
			logger.error("Failed to retireve CTA alerts", e);
		}
		return null;
	}
	
	public static CTAArrivals getCTAArrivals(String stationId, String maxArrivals){
		InputStream fetchedResultsStream = null;
		try {
			URI uriToFetchData = CTAUtil.getCTAArrivalsURI(stationId, maxArrivals).build();
			logger.debug(uriToFetchData);
			fetchedResultsStream = CTAXmlParser.processAPIRequest(uriToFetchData);
			XmlMapper xmlMapper = new XmlMapper();
			xmlMapper.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);
			return xmlMapper.readValue(fetchedResultsStream, CTAArrivals.class);
		} catch (Exception e) {
			logger.error("Failed to retireve CTA arrivals for " + stationId, e);
		}
		return null;
	}
	
	public static CTABusDirections getBusRouteDirections(String routeNumber){
		InputStream fetchedResultsStream = null;
		try {
			URI uriToFetchData = CTABusUtil.getBusRouteDirectionsURI(routeNumber).build();
			logger.debug(uriToFetchData.toURL().toString());
			fetchedResultsStream = CTAXmlParser.processAPIRequest(uriToFetchData);
			XmlMapper xmlMapper = new XmlMapper();
			return xmlMapper.readValue(fetchedResultsStream, CTABusDirections.class);
		} catch (Exception e) {
			logger.error("Failed to retireve cardinal directions for bus route: " + routeNumber, e);
		}
		return null;
	}
	
	public static CTABusRoutes getBusRoutes(){
		InputStream fetchedResultsStream = null;
		try {
			URI uriToFetchData = CTABusUtil.getBusRoutes().build();
			logger.debug(uriToFetchData.toURL().toString());
			fetchedResultsStream = CTAXmlParser.processAPIRequest(uriToFetchData);
			XmlMapper xmlMapper = new XmlMapper();
			xmlMapper.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);
			return xmlMapper.readValue(fetchedResultsStream, CTABusRoutes.class);
		} catch (Exception e) {
			logger.error("Failed to retireve CTA Bus routes", e);
		}
		return null;
	}
	
	public static CTABusStops getBusStops(String routeNumber, String direction){
		InputStream fetchedResultsStream = null;
		try {
			URI uriToFetchData = CTABusUtil.getBusRouteStops(routeNumber, direction).build();
			logger.debug(uriToFetchData.toURL().toString());
			fetchedResultsStream = CTAXmlParser.processAPIRequest(uriToFetchData);
			XmlMapper xmlMapper = new XmlMapper();
			xmlMapper.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);
			return xmlMapper.readValue(fetchedResultsStream, CTABusStops.class);
		} catch (Exception e) {
			logger.error("Failed to retireve CTA Bus stops", e);
		}
		return null;
	}
	
	public static CTABusArrivals getBusPredictions(String stopId, String maxResults){
		InputStream fetchedResultsStream = null;
		try{
			URI uriToFetchData = CTABusUtil.getBusStopPredictions(stopId, "", maxResults).build();
			logger.debug(uriToFetchData.toURL().toString());
			fetchedResultsStream = CTAXmlParser.processAPIRequest(uriToFetchData);
			XmlMapper xmlMapper = new XmlMapper();
			xmlMapper.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);
			return xmlMapper.readValue(fetchedResultsStream, CTABusArrivals.class);
		}catch(Exception e){
			logger.error("Failed to retireve CTA Bus arrival predictions for " + stopId, e);
		}
		return null;
	}
	
	/**
	 * Util method for making 
	 * @param type
	 * @return
	 */
	public static InputStream processAPIRequest(URI uri){
		URI uriToFetchData = uri;
		HttpGet request;
		try {
			request = new HttpGet(uriToFetchData);
			// add request header
			request.addHeader(HttpHeaders.USER_AGENT, "Mozilla/5.0");
			request.addHeader(HttpHeaders.CONTENT_TYPE, "text/xml; charset=utf-8");
			//make sure to reset the connection - might hang otherwise
			HttpClient client = CTAXmlParser.getHttpClient();
			HttpResponse responseTrainStations = client.execute(request);
			HttpEntity entity = responseTrainStations.getEntity(); 
			return entity.getContent();
		} catch (Exception e) {
			logger.debug("Failed to fetch data at: " + uriToFetchData.toString(), e);
		}
		return null;
	}
}
