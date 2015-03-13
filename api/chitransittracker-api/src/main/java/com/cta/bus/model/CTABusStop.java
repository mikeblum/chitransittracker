package com.cta.bus.model;

import org.apache.commons.lang.StringUtils;

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
	
	private String routeNumber;
	private String routeName;
	private String routeColor;
	private String type;
	private String direction;
	
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
	public String getRouteNumber() {
		return routeNumber;
	}
	public void setRouteNumber(String routeNumber) {
		this.routeNumber = routeNumber;
	}
	public String getRouteName() {
		return routeName;
	}
	public void setRouteName(String routeName) {
		this.routeName = routeName;
	}
	public String getRouteColor() {
		return routeColor;
	}
	public void setRouteColor(String routeColor) {
		this.routeColor = routeColor;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getDirection() {
		return direction;
	}
	public void setDirection(String direction) {
		this.direction = direction;
	}
	
	public String[] getAttributes(){
		String[] attributes = {
			this.stopId,
			this.stopNumber,
			StringUtils.join(new String[]{this.latitude, this.longitude}, ", "),
			this.routeNumber,
			this.routeName,
			this.routeColor,
			this.direction,
			this.type
		};
		return attributes;
	}
}
