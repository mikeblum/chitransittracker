package com.cta.util;

import org.apache.http.client.utils.URIBuilder;
import org.apache.log4j.Logger;

public class CTABusUtil extends CTAUtil{
	static Logger logger = Logger.getLogger(CTABusUtil.class);
	protected static String base = "www.ctabustracker.com/bustime/api/";
	protected static String version = "v1";
	
	protected static final String DIRECTIONS = "getdirections";
	protected static final String STOPS		 = "getstops";
	protected static final String PREDICTIONS = "getpredictions";
	protected static final String ROUTES = "getroutes";
	
	/**
	 * Base CTA API url - expects the caller to add the API version, path, and any parameters
	 * @return spring-injected cta.api.uri.scheme + cta.api.uri.base
	 */
	public static URIBuilder getAPIBase(){
		try{
			return new URIBuilder()
			.setScheme(CTABusUtil.scheme)
			.setHost(CTABusUtil.base);
		}catch(Exception e){
			logger.debug("Failed to build uri for CTA Bus api.", e);
		}
		return null;
	}
	
	protected static URIBuilder getBusRouteDirectionsURI(String routeNumber){
		URIBuilder busRouteDirectionsURI = CTABusUtil.getAPIBase();
		busRouteDirectionsURI.setPath(CTABusUtil.version + "/" + CTABusUtil.DIRECTIONS)
				 .setParameter("key", CTAUtil.getCTA_API_BUS_KEY())
				 .setParameter("rt", routeNumber);
		return busRouteDirectionsURI;
	}
	
	protected static URIBuilder getBusRouteStops(String routeNumber, String direction){
		URIBuilder busRouteStopsURI = CTABusUtil.getAPIBase();
		busRouteStopsURI.setPath(CTABusUtil.version + "/" + CTABusUtil.STOPS)
				 .setParameter("key", CTAUtil.getCTA_API_BUS_KEY())
				 .setParameter("rt", routeNumber)
				 .setParameter("dir", direction);
		return busRouteStopsURI;
	}
	
	protected static URIBuilder getBusStopPredictions(String routeNumber, String stopId, String maxResults){
		URIBuilder busRouteStopsURI = CTABusUtil.getAPIBase();
		busRouteStopsURI.setPath(CTABusUtil.version + "/" + CTABusUtil.PREDICTIONS)
				 .setParameter("key", CTAUtil.getCTA_API_BUS_KEY())
				 .setParameter("rt", routeNumber)
				 .setParameter("stopid", stopId)
				 .setParameter("top", maxResults);
		return busRouteStopsURI;
	}
	
	protected static URIBuilder getBusRoutes(){
		URIBuilder busRoutesURI = CTABusUtil.getAPIBase();
		busRoutesURI.setPath(CTABusUtil.version + "/" + CTABusUtil.ROUTES)
		 			.setParameter("key", CTAUtil.getCTA_API_BUS_KEY());
		return busRoutesURI;
	}
	
	
}
