package com.chitransittracker.rest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.cta.bus.model.CTABusArrival;
import com.cta.bus.model.CTABusArrivals;
import com.cta.model.CTAArrival;
import com.cta.model.CTAArrivals;
import com.cta.util.CTAXmlParser;

@RestController
public class RESTArrivals {
	
	Logger logger = Logger.getLogger(RESTArrivals.class);
	TypeReference typeRef = new TypeReference<HashMap<String,String>>(){};
	
	@RequestMapping(value="/train/arrivals", method=RequestMethod.GET)
	public List<Map<String, String>> getArrivals(
			 @RequestParam(value="id", required=true) String routeId,
			 @RequestParam(value="max", required=false) String numArrivals,
			 HttpServletResponse response
	     ){
		
		 try{
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
		 }catch(Exception e){
			 response.setStatus(500);
			 logger.error("Failed to get arrivals for " + routeId);
			 Map<String, String> errorMessage = new HashMap<String, String>();
			 errorMessage.put("error", "Failed to get arrivals for " + routeId);
			 List<Map<String, String>> errorWrapper = new ArrayList<Map<String, String>>();
			 errorWrapper.add(errorMessage);
			 return errorWrapper;
		 }
	 }
	
	@RequestMapping(value="/bus/arrivals", method=RequestMethod.GET)
	public List<Map<String, String>> getBusArrivals(
			 @RequestParam(value="id", required=true) String stopId,
			 @RequestParam(value="max", required=false) String numArrivals,
			 HttpServletResponse response
	     ){
		 try{
			 //default to 5 arrivals
			 if(StringUtils.isBlank(numArrivals)){
				 numArrivals = "5";
			 }
			 //fetch arrivals
			 CTABusArrivals arrivals = CTAXmlParser.getBusPredictions(stopId, numArrivals);
			 List<Map<String, String>> arrivalsSerialized = new ArrayList<Map<String, String>>();
			 Iterator<CTABusArrival> arrivalsItr = arrivals.getArrivals().iterator();
			 ObjectMapper mapper = new ObjectMapper();
			 while(arrivalsItr.hasNext()){
				 Map<String,String> jsonMap = new HashMap<String,String>();
				 try {
					 //convert JSON string to Map
					 jsonMap = mapper.readValue(arrivalsItr.next().toJSON().toString(), typeRef);
				 }catch (Exception e) {
					logger.error("Failed to cnovert CTABusArrivals to JSON", e);
				 }
				 arrivalsSerialized.add(jsonMap);
			 }
			 return arrivalsSerialized;
		 }catch(Exception e){
			 response.setStatus(500);
			 logger.error("Failed to get arrivals for " + stopId);
			 Map<String, String> errorMessage = new HashMap<String, String>();
			 errorMessage.put("error", "Failed to get arrivals for " + stopId);
			 List<Map<String, String>> errorWrapper = new ArrayList<Map<String, String>>();
			 errorWrapper.add(errorMessage);
			 return errorWrapper;
		 }
	 }
}