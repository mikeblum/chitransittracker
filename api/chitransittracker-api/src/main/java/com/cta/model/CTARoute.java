package com.cta.model;

import java.net.URI;
import java.util.ArrayList;

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;


public class CTARoute {
	
	@JacksonXmlProperty(localName = "Route")
	private String routeName;
	@JacksonXmlProperty(localName = "RouteColorCode")
	private String routeColorCode;
	@JacksonXmlProperty(localName = "RouteTextColor")
	private String routeTextColor;
	@JacksonXmlProperty(localName = "ServiceId")
	private String serviceId;
	@JacksonXmlProperty(localName = "RouteURL")
	private URI routeUrl;
	@JacksonXmlProperty(localName = "RouteStatus")
	private String routeStatus;
	@JacksonXmlProperty(localName = "RouteStatusColor")
	private String routeStatusColor;
	
	public String getRouteName() {
		return routeName;
	}
	public void setRouteName(String routeName) {
		this.routeName = routeName;
	}
	public String getRouteColorCode() {
		return routeColorCode;
	}
	public void setRouteColorCode(String routeColorCode) {
		this.routeColorCode = routeColorCode;
	}
	public String getRouteTextColor() {
		return routeTextColor;
	}
	public void setRouteTextColor(String routeTextColor) {
		this.routeTextColor = routeTextColor;
	}
	public String getServiceId() {
		return serviceId;
	}
	public void setServiceId(String serviceId) {
		this.serviceId = serviceId;
	}
	public URI getRouteUrl() {
		return routeUrl;
	}
	public void setRouteUrl(URI routeUrl) {
		this.routeUrl = routeUrl;
	}
	public String getRouteStatus() {
		return routeStatus;
	}
	public void setRouteStatus(String routeStatus) {
		this.routeStatus = routeStatus;
	}
	public String getRouteStatusColor() {
		return routeStatusColor;
	}
	public void setRouteStatusColor(String routeStatusColor) {
		this.routeStatusColor = routeStatusColor;
	}
	
	public String[] getAttributes(){
		String[] attributes = new String[7];
		attributes[0] = this.routeName;
		attributes[1] = this.routeColorCode;
		attributes[2] = this.routeTextColor;
		attributes[3] = this.serviceId;
		attributes[4] = this.routeUrl.toString();
		attributes[5] = this.routeStatus;
		attributes[6] = this.routeStatusColor;
		return attributes;
	}
	
	/**
	 * get comma-seperated list of attributes
	 */
	public String toString(){
		return StringUtils.join(getAttributes(), ',');
	}
}
