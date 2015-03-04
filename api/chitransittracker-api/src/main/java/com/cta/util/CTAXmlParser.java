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
import com.cta.model.CTARoutes;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;

public class CTAXmlParser {
	static Logger logger = Logger.getLogger(CTAXmlParser.class);
	
	protected static HttpClient client = HttpClientBuilder.create().build();
	protected static RequestConfig requestConfig = RequestConfig.custom().setConnectTimeout(30 * 1000).build();
	/**
	 * Given a type, deserialize CTA XML to POJOs
	 * @param type rail, bus, station, or systemwide
	 * @return deserialzed XML routes as POJOs
	 */
	public static CTARoutes getCTARoutesInfo(String type){
		try {
			URI uriToFetchData = CTAXmlParser.getCTARoutesURI(type).build();
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
			URI uriToFetchData = CTAXmlParser.getCTAAlertsURI().build();
			InputStream fetchedResultsStream = processAPIRequest(uriToFetchData);
			XmlMapper xmlMapper = new XmlMapper();
			xmlMapper.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);
			return xmlMapper.readValue(fetchedResultsStream, CTAAlerts.class);
		} catch (Exception e) {
			logger.error("Failed to retireve CTA alerts", e);
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
			
			client = HttpClientBuilder.create().setDefaultRequestConfig(requestConfig).build();
			HttpResponse responseTrainStations = client.execute(request);
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
			.setScheme(CTAUtil.scheme)
			.setHost(CTAUtil.base);
		}catch(Exception e){
			logger.debug("Failed to build uri for CTA api.", e);
		}
		return null;
	}
	
	/**
	 * Get the fully qualified url to this CTA API type endpoint: station, bus, rail, or systemwide
	 * @param type station, bus, rail, or systemwide
	 * @return fully qualified url like this: http://lapi.transitchicago.com/api/1.0/routes.aspx?type=systemwide
	 */
	public static URIBuilder getCTARoutesURI(String type){
		URIBuilder routesURI = getAPIBase();
		routesURI.setPath(CTAUtil.version + "/" + CTAUtil.routes)
				 .setParameter("type", type);
		return routesURI;
	}
	
	/**
	 * Get the fully qualified alerts url from CTA
	 * @return fully qualified url to the alerts endpoint
	 */
	public static URIBuilder getCTAAlertsURI(){
		URIBuilder alertsURI = getAPIBase();
		alertsURI.setPath(CTAUtil.version + "/" + CTAUtil.alerts);
		return alertsURI;
	}
}
