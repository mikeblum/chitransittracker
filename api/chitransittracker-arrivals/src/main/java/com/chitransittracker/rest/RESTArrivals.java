package com.chitransittracker.rest;

import org.apache.log4j.Logger;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.cta.util.CTAUtil;

@RestController
public class RESTArrivals {
	
	Logger logger = Logger.getLogger(RESTArrivals.class);
	
	 @RequestMapping("/station")
	 public void getArrivals(@RequestParam(value="name") String name){
		 logger.debug(CTAUtil.getCTA_API_TRAIN_KEY());
	 }
}
