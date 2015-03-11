package com.cta.util;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.utils.URIBuilder;
import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

public class CTAUtil {	
	
	private static Logger logger = Logger.getLogger(CTAUtil.class);
	public static DateTimeFormatter CTA_DATE_TIME_HMS = DateTimeFormat.forPattern("yyyyMMdd HH:mm:ss");
	public static DateTimeFormatter CTA_DATE_TIME_HM = DateTimeFormat.forPattern("yyyyMMdd HH:mm");
	public static DateTimeFormatter CTA_DATE = DateTimeFormat.forPattern("yyyyMMdd");
	
	private static String CTA_API_BUS_KEY;
	private static String CTA_API_TRAIN_KEY;
	
	public static String RAIL = "rail";
	public static String BUS = "bus";
	public static String STATION = "station";
	
	protected static String user_agent = "Mozilla/5.0";
	protected static String scheme = "http";
	protected static String base = "lapi.transitchicago.com/api/";
	protected static String version = "1.0";
	
	
	//cta aspx endpoints
	protected static String routes = "routes.aspx";
	protected static String alerts = "alerts.aspx";
	protected static String arrivals = "ttarrivals.aspx";

	public static String getRAIL() {
		return RAIL;
	}

	public static String getBUS() {
		return BUS;
	}

	public static String getSTATION() {
		return STATION;
	}

	public static String getCTA_API_BUS_KEY() {
		CTA_API_BUS_KEY = System.getenv("CTA_API_BUS_KEY");
		return CTA_API_BUS_KEY;
	}
	
	public static String getCTA_API_TRAIN_KEY() {
		CTA_API_TRAIN_KEY = System.getenv("CTA_API_TRAIN_KEY");
		return CTA_API_TRAIN_KEY;
	}
	
	/**
	 * CTA uses a variety of date time precisions in their XML
	 * This util attempts to convert a stringified datetime stamp to a more computational datetime Joda timestamp
	 * 
	 * @param timestamp stringified date time stamp
	 */
	public static DateTime parseDateTime(String timestamp){
		DateTime result = null;
		if(timestamp == null || StringUtils.equalsIgnoreCase(timestamp, "n/a")){
			result = new DateTime();
		}
		try{
			result = CTA_DATE_TIME_HMS.parseDateTime(timestamp);
		}catch(Exception hmsExpcetion){
			try{
				result = CTA_DATE_TIME_HM.parseDateTime(timestamp);
			}catch(Exception hmException){
				result = CTA_DATE.parseDateTime(timestamp);
			}
		}
		return result;
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
	
	/**
	 * Get the fully qualified arrivals url from CTA
	 * @param stationId valid CTA station id
	 * @param maxArrivals 1-5 arrivals returned for this request
	 * @return fully qualified url with the station id id, api key, and max arrivals built up
	 */
	public static URIBuilder getCTAArrivalsURI(String stationId, String maxArrivals){
		URIBuilder arrivalsURI = getAPIBase();
		arrivalsURI.setPath(CTAUtil.version + "/" + CTAUtil.arrivals)
				   .setParameter("key", CTAUtil.getCTA_API_TRAIN_KEY())
				   .setParameter("max", maxArrivals)
				   .setParameter("mapid", stationId);
		return arrivalsURI;
	}
}