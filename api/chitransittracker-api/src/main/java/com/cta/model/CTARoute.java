package com.cta.model;

import java.net.URI;
import java.util.ArrayList;

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;


public class CTARoute {
	
	@JacksonXmlProperty(localName = "Route")
	private String routeName;
	private String address;
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
	private String type;
	
	public void setType(String type){
		this.type = type;
	}
	
	public String getRouteName() {
		return routeName;
	}

	public String getAddress() {
		return address;
	}

	public String getRouteColorCode() {
		return routeColorCode;
	}

	public String getRouteTextColor() {
		return routeTextColor;
	}

	public String getServiceId() {
		return serviceId;
	}

	public URI getRouteUrl() {
		return routeUrl;
	}

	public String getRouteStatus() {
		return routeStatus;
	}

	public String getRouteStatusColor() {
		return routeStatusColor;
	}

	public String[] getAttributes(){
		//example route name
		//35th-Bronzeville-IIT | 16 E. 35th St., Chicago, IL 60616
		//split so that searching occurs on the station name OR the address
		String[] routeParts = StringUtils.split(this.routeName, '|');
		//replace all ampersands with and
		//Museum of Science & Industry
		String routeName = StringUtils.trim(routeParts[0]);
		routeName = StringUtils.replace(routeName, "&", "and");
		String[] attributes = {
				routeName,
				routeParts.length == 2 ? StringUtils.trim(routeParts[1]) : "",
				this.routeColorCode,
				this.routeTextColor,
				this.serviceId,
				this.routeUrl.toString(),
				this.routeStatus,
				this.routeStatusColor,
				this.type
		};
		
		return attributes;
	}
	
	/**
	 * get comma-seperated list of attributes
	 */
	public String toString(){
		return StringUtils.join(getAttributes(), ',');
	}
}
