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

	public String getCTA_API_BUS_KEY() {
		return CTA_API_BUS_KEY;
	}

	public void setCTA_API_BUS_KEY(String cTA_API_BUS_KEY) {
		CTA_API_BUS_KEY = cTA_API_BUS_KEY;
	}

	public String getCTA_API_TRAIN_KEY() {
		return CTA_API_TRAIN_KEY;
	}

	public void setCTA_API_TRAIN_KEY(String cTA_API_TRAIN_KEY) {
		CTA_API_TRAIN_KEY = cTA_API_TRAIN_KEY;
	}
}