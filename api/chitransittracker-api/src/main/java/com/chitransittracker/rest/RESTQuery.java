package com.chitransittracker.rest;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.codehaus.jackson.type.TypeReference;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.chitransittracker.solr.impl.SolrQueryImpl;

/**
 * Endpoints for exposing all CTA Bus, Train, and Rail Lines
 * @author mblum
 *
 */
@RestController
public class RESTQuery {
	Logger logger = Logger.getLogger(RESTArrivals.class);

	@RequestMapping(value="/query/stops/nearby", method=RequestMethod.GET)
	public List<Map<String, String>> getNearbyStops(
			 @RequestParam(value="loc", required=true) String location,
			 @RequestParam(value="max", required=false) String numResults
	     ){
		return SolrQueryImpl.getNearbyStops(location);
	}
	
	@RequestMapping(value="/query/stops", method=RequestMethod.GET)
	public List<Map<String, String>> getQueriedStops(
			 @RequestParam(value="q", required=true) String query,
			 @RequestParam(value="max", required=false) String numResults
		){
		return SolrQueryImpl.getQueryableStops(query);
	}
	
	@RequestMapping(value="/query/alerts", method=RequestMethod.GET)
	public List<Map<String, String>> getQueriedAlerts(
			 @RequestParam(value="q", required=true) String query,
			 @RequestParam(value="max", required=false) String numResults
		){
		return SolrQueryImpl.getQueryableAlerts(query);
	}
	
	
}
