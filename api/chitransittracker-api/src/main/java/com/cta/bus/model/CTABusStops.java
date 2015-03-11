package com.cta.bus.model;

import java.util.List;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

@JacksonXmlRootElement(localName = "bustime-response")
public class CTABusStops {
	@JacksonXmlProperty(localName = "stop")
	//unwrapping is important since each stop is is unwrapped
	@JacksonXmlElementWrapper(useWrapping = false)
	private List<CTABusStop> busStops;
	
	public List<CTABusStop> getBusStops(){
		return this.busStops;
	}
}
