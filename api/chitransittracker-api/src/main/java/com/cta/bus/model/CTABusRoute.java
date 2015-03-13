package com.cta.bus.model;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

public class CTABusRoute {
	@JacksonXmlProperty(localName = "rt")
	private String routeNumber;
	@JacksonXmlProperty(localName = "rtnm")
	private String routeName;
	@JacksonXmlProperty(localName = "rtclr")
	private String routeColor;
	private String type;
	
	public String getRouteNumber() {
		return routeNumber;
	}
	public String getRouteName() {
		return routeName;
	}
	public String getRouteColor() {
		return routeColor;
	}
	
	public void setType(String type){
		this.type = type;
	}
	
	public String getType(){
		return type;
	}
}
