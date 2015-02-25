package com.cta.model;

import java.util.List;

import org.joda.time.DateTime;

import com.cta.util.CTAUtil;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

@JacksonXmlRootElement(localName = "CTARoutes")
public class CTARoutes {
	@JacksonXmlProperty(localName = "TimeStamp")
	DateTime timeStamp;
	@JacksonXmlProperty(localName = "ErrorCode")
	String errorCode;
	@JacksonXmlProperty(localName = "ErrorMessage")
	String errorMessage;
	@JacksonXmlProperty(localName = "RouteInfo")
	//unwrapping is important since the RouteInfo objects have no wrapper
	@JacksonXmlElementWrapper(useWrapping = false)
	List<CTARoute> routes;
	
	public List<CTARoute> getRoutes() {
		return routes;
	}

	public void setRoutes(List<CTARoute> routes) {
		this.routes = routes;
	}

	public void setTimeStamp(String timeStamp) {
		this.timeStamp = CTAUtil.CTA_DATE_TIME.parseDateTime(timeStamp);
	}
}
