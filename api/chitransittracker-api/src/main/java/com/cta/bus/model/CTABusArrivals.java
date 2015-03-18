package com.cta.bus.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

@JsonIgnoreProperties(ignoreUnknown = true)
@JacksonXmlRootElement(localName = "bustime-response")
public class CTABusArrivals {
	//this route has the following directions
	@JacksonXmlProperty(localName = "prd")
	//unwrapping is important since each direction is a single line
	@JacksonXmlElementWrapper(useWrapping = false)
	private List<CTABusArrival> arrivals;
	
	public List<CTABusArrival> getArrivals(){
		return this.arrivals;
	}
}
