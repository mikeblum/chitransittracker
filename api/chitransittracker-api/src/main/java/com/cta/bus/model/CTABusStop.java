package com.cta.bus.model;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

/**
 * POJO for serializing a CTA Bus Stop
 * 
 * @author mblum
 *
 */
public class CTABusStop {
	@JacksonXmlProperty(localName = "stpid")
	private String stopId;
	@JacksonXmlProperty(localName = "stpnm")
	private String stopNumber;
	@JacksonXmlProperty(localName = "lat")
	private String latitude;
	@JacksonXmlProperty(localName = "lon")
	private String longitude;
	
	public String getStopId() {
		return stopId;
	}
	public String getStopNumber() {
		return stopNumber;
	}
	public String getLatitude() {
		return latitude;
	}
	public String getLongitude() {
		return longitude;
	}
}
