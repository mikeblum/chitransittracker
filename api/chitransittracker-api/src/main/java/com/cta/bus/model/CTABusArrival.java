package com.cta.bus.model;

import org.joda.time.DateTime;
import org.json.JSONObject;

import com.cta.util.CTAUtil;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class CTABusArrival {
	@JacksonXmlProperty(localName = "tmstmp")
	private DateTime timestamp;
	@JacksonXmlProperty(localName = "typ")
	private String type;
	@JacksonXmlProperty(localName = "stpnm")
	private String stopName;
	@JacksonXmlProperty(localName = "stpid")
	private String stopId;
	@JacksonXmlProperty(localName = "vid")
	private String vehicleId;
	@JacksonXmlProperty(localName = "dstp")
	private String feetToStop;
	@JacksonXmlProperty(localName = "rt")
	private String routeNumber;
	@JacksonXmlProperty(localName = "rtdir")
	private String routeDirection;
	@JacksonXmlProperty(localName = "des")
	private String destination;
	@JacksonXmlProperty(localName = "prdtm")
	private DateTime arrivalTime;
	
	public void setTimestamp(String timestamp){
		this.timestamp = CTAUtil.parseDateTime(timestamp);
	}
	
	public void setArrivalTime(String timestamp){
		this.arrivalTime = CTAUtil.parseDateTime(timestamp);
	}
	
	public JSONObject toJSON(){
		JSONObject json = new JSONObject();
		json.put("timestamp", this.timestamp.toDateTimeISO());
		json.put("type", this.type);
		json.put("stopId", this.stopId);
		json.put("stopName", this.stopName);
		json.put("vehicleId", this.vehicleId);
		json.put("feetToStop", this.feetToStop);
		json.put("routeNumber", this.routeNumber);
		json.put("direction", this.routeDirection);
		json.put("destination", this.destination);
		json.put("arrivalTime", this.arrivalTime.toDateTimeISO());
		return json;
	}
}
