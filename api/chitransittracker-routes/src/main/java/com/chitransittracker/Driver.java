package com.chitransittracker;

import java.sql.Connection;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import com.chitransittracker.jdbc.ConnectionDetails;
import com.chitransittracker.jdbc.PGConnector;
import com.cta.model.CTARoutes;
import com.cta.util.CTAUtil;
import com.cta.util.CTAXmlParser;

public class Driver {
	static Logger logger = Logger.getLogger(Driver.class);
	
	@Autowired
	static ConnectionDetails injectedDetails;
	static CTAXmlParser ctaParser;
	
	public static void main(String[] args){
		//stand up Spring Context
		ApplicationContext context = AppContext.startContext();
		injectedDetails = (ConnectionDetails) context.getBean("connectionDetails");
		Connection testConnection = new PGConnector(injectedDetails).getDBConnection();
		ctaParser = new CTAXmlParser();
		CTARoutes ctaRailLines = ctaParser.getCTARoutesInfo(CTAUtil.RAIL);
		logger.debug("CTA Stations: " + ctaRailLines.getRoutes().size());
	}
}