package com.cta.util;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Component;

import com.chitransittracker.jdbc.ConnectionDetails;
import com.chitransittracker.jdbc.PGConnector;
import com.cta.model.CTARoutes;

@Component
public class CTAIndexer {
	static Logger logger = Logger.getLogger(CTAIndexer.class);
	
	@Autowired
	static ConnectionDetails injectedDetails;
	@Autowired
	static CTAXmlParser ctaParser;
	
	private static ApplicationContext startContext(){
		 return new ClassPathXmlApplicationContext("applicationContext.xml");
	}
	
	public static void main(String[] args){
		//stand up Spring Context
		ApplicationContext context = startContext();
		injectedDetails = (ConnectionDetails) context.getBean("connectionDetails");
		Connection testConnection = new PGConnector(injectedDetails).getDBConnection();
		try {
			Statement simpleStatement = testConnection.createStatement();
			boolean simpleResults = simpleStatement.execute("SELECT 1");
		} catch (SQLException e) {
			logger.error("Failed to connect to local postgres db", e);
		}
		ctaParser = (CTAXmlParser) context.getBean("CTAXmlParser");
		CTARoutes ctaRailLines = ctaParser.getCTARoutesInfo(CTAXmlParser.RAIL);
	}
}
