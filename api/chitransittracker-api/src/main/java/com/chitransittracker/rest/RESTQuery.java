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

/**
 * Endpoints for exposing all CTA Bus, Train, and Rail Lines
 * @author mblum
 *
 */
@RestController
public class RESTQuery {
	Logger logger = Logger.getLogger(RESTArrivals.class);
	TypeReference typeRef = new TypeReference<HashMap<String,String>>(){};
	
	@RequestMapping(value="/query", method=RequestMethod.GET)
	public List<Map<String, String>> getRoutes(
			 @RequestParam(value="id", required=true) String routeQuery,
			 @RequestParam(value="max", required=false) String numResults
	     ){
		return null;
	}
}
