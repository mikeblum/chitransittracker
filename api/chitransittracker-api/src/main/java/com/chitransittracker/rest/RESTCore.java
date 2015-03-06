package com.chitransittracker.rest;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RESTCore {
	
	Logger logger = Logger.getLogger(RESTCore.class);
	
	 @RequestMapping(value="/", method=RequestMethod.GET)
	 public Map<String, String> getArrivals(){
		 Map<String, String> map = new HashMap<String, String>();
		 return map;
	 }
}