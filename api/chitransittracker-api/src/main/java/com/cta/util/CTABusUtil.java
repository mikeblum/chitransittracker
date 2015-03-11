package com.cta.util;

import org.apache.http.client.utils.URIBuilder;

public class CTABusUtil extends CTAUtil{
	protected static String base = "http://www.ctabustracker.com/bustime/api/";
	protected static String version = "v1";
	
	protected static final String DIRECTIONS = "getdirections";
	protected static final String STOPS		 = "getstops";
	protected static final String PREDICTIONS = "getpredictions";
	
	protected static URIBuilder getBusRouteDirectionsURI(String routeNumber){
		URIBuilder busRouteDirectionsURI = getAPIBase();
		busRouteDirectionsURI.setPath(CTAUtil.version + "/" + CTABusUtil.DIRECTIONS)
				 .setParameter("key", CTAUtil.getCTA_API_BUS_KEY())
				 .setParameter("rt", routeNumber);
		return busRouteDirectionsURI;
	}
	
	protected static URIBuilder getBusRouteStops(String routeNumber, String direction){
		URIBuilder busRouteStopsURI = getAPIBase();
		busRouteStopsURI.setPath(CTAUtil.version + "/" + CTABusUtil.DIRECTIONS)
				 .setParameter("key", CTAUtil.getCTA_API_BUS_KEY())
				 .setParameter("rt", routeNumber)
				 .setParameter("dir", direction);
		return busRouteStopsURI;
	}
	
	protected static URIBuilder getBusStopPredictions(String routeNumber, String stopId, String maxResults){
		URIBuilder busRouteStopsURI = getAPIBase();
		busRouteStopsURI.setPath(CTAUtil.version + "/" + CTABusUtil.PREDICTIONS)
				 .setParameter("key", CTAUtil.getCTA_API_BUS_KEY())
				 .setParameter("rt", routeNumber)
				 .setParameter("stopid", stopId)
				 .setParameter("top", maxResults);
		return busRouteStopsURI;
	}
	
	
}
