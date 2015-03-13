package com.cta.bus.model;

import java.util.List;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

@JacksonXmlRootElement(localName = "bustime-response")
public class CTABusRoutes {
	@JacksonXmlProperty(localName = "error")
	private String error;
	@JacksonXmlProperty(localName = "route")
	//unwrapping is important since each stop is is unwrapped
	@JacksonXmlElementWrapper(useWrapping = false)
	private List<CTABusRoute> busRoutes;
	
	public List<CTABusRoute> getBusRoutes(){
		return busRoutes;
	}
}
