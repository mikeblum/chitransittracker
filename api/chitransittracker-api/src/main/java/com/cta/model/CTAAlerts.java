package com.cta.model;

import java.util.List;

import org.joda.time.DateTime;

import com.cta.util.CTAUtil;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

@JacksonXmlRootElement(localName = "CTAAlerts")
public class CTAAlerts {
	@JacksonXmlProperty(localName = "TimeStamp")
	DateTime timeStamp;
	@JacksonXmlProperty(localName = "ErrorCode")
	String errorCode;
	@JacksonXmlProperty(localName = "ErrorMessage")
	String errorMessage;
	@JacksonXmlProperty(localName = "Alert")
	//unwrapping is important since the RouteInfo objects have no wrapper
	@JacksonXmlElementWrapper(useWrapping = false)
	List<CTAAlert> alerts;
	
	public List<CTAAlert> getAlerts() {
		return alerts;
	}
	
	public void setTimeStamp(String timeStamp) {
		this.timeStamp = CTAUtil.CTA_DATE_TIME.parseDateTime(timeStamp);
	}
}
