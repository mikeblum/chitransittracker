package com.cta.util;

import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.springframework.beans.factory.annotation.Value;

public class CTAUtil {														   
	public static DateTimeFormatter CTA_DATE_TIME = DateTimeFormat.forPattern("YYYYMMDD HH:mm");
	
	@Value("${cta.api.bus.key}")
	private String CTA_API_BUS_KEY;
	
	@Value("${cta.api.train.key}")
	private String CTA_API_TRAIN_KEY;
}