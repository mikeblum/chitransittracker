package com.cta.util;

import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

public class CTAUtil {														   
	public static DateTimeFormatter CTA_DATE_TIME = DateTimeFormat.forPattern("YYYYMMDD HH:mm");
	
	@Value("#{ systemEnvironment['CTA_API_BUS_KEY'] }")
	private static String CTA_API_BUS_KEY;
	@Value("#{ systemEnvironment['CTA_API_TRAIN_KEY'] }")
	private static String CTA_API_TRAIN_KEY;
	
	public static String RAIL = "rail";
	public static String BUS = "bus";
	public static String STATION = "station";
	
	protected static String user_agent = "Mozilla/5.0";
	protected static String scheme = "http";
	protected static String base = "lapi.transitchicago.com/api/";
	protected static String version = "1.0";
	
	//cta aspx endpoints
	protected static String routes = "routes.aspx";
	protected static String alerts = "alerts.aspx";

	public static String getRAIL() {
		return RAIL;
	}

	public static String getBUS() {
		return BUS;
	}

	public static String getSTATION() {
		return STATION;
	}

	public static String getCTA_API_BUS_KEY() {
		return CTA_API_BUS_KEY;
	}

	public void setCTA_API_BUS_KEY(String cTA_API_BUS_KEY) {
		CTA_API_BUS_KEY = cTA_API_BUS_KEY;
	}
	
	public static String getCTA_API_TRAIN_KEY() {
		return CTA_API_TRAIN_KEY;
	}

	public void setCTA_API_TRAIN_KEY(String cTA_API_TRAIN_KEY) {
		CTA_API_TRAIN_KEY = cTA_API_TRAIN_KEY;
	}
}