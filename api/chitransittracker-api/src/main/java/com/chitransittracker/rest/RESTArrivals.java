package com.chitransittracker.rest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.cta.model.CTAArrival;
import com.cta.model.CTAArrivals;
import com.cta.util.CTAXmlParser;

@RestController
public class RESTArrivals {
	
	Logger logger = Logger.getLogger(RESTArrivals.class);
	TypeReference typeRef = new TypeReference<HashMap<String,String>>(){};
	
	@RequestMapping(value="/arrivals", method=RequestMethod.GET)
	public List<Map<String, String>> getArrivals(
			 @RequestParam(value="id", required=true) String routeId,
			 @RequestParam(value="max", required=false) String numArrivals
	     ){
		 //default to 5 arrivals
		 if(StringUtils.isBlank(numArrivals)){
			 numArrivals = "5";
		 }
		 //fetch arrivals
		 CTAArrivals arrivals = CTAXmlParser.getCTAArrivals(routeId, numArrivals);
		 List<Map<String, String>> arrivalsSerialized = new ArrayList<Map<String, String>>();
		 Iterator<CTAArrival> arrivalsItr = arrivals.getArrivals().iterator();
		 ObjectMapper mapper = new ObjectMapper();
		 while(arrivalsItr.hasNext()){
			 Map<String,String> jsonMap = new HashMap<String,String>();
			 try {
				 //convert JSON string to Map
				 jsonMap = mapper.readValue(arrivalsItr.next().toJSON().toString(), typeRef);
			 }catch (Exception e) {
				logger.error("Failed to cnovert CTAArrivals to JSON", e);
			 }
			 arrivalsSerialized.add(jsonMap);
		 }
		 return arrivalsSerialized;
	 }
}