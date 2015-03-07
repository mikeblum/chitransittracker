package com.cta.model;

import java.util.List;

import org.joda.time.DateTime;
import org.json.JSONObject;

import com.cta.util.CTAUtil;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

public class CTAArrival {
	@JacksonXmlProperty(localName = "staId")
	private String stationId;
	@JacksonXmlProperty(localName = "stpId")
	private String stopId;
	@JacksonXmlProperty(localName = "staNm")
	private String stationName;
	@JacksonXmlProperty(localName = "stpDe")
	private String stationDesc;
	@JacksonXmlProperty(localName = "rn")
	private String runNumber;
	@JacksonXmlProperty(localName = "rt")
	private String route;
	@JacksonXmlProperty(localName = "destSt")
	private String destinationSt;
	@JacksonXmlProperty(localName = "destNm")
	private String destinationName;
	@JacksonXmlProperty(localName = "trDr")
	private String direction;
	@JacksonXmlProperty(localName = "prdt")
	private DateTime generated;
	@JacksonXmlProperty(localName = "arrT")
	private DateTime arrivalTime;
	@JacksonXmlProperty(localName = "isApp")
	private boolean isApproching;
	@JacksonXmlProperty(localName = "isSch")
	private boolean isScheduled;
	@JacksonXmlProperty(localName = "isDly")
	private boolean isDelayed;
	@JacksonXmlProperty(localName = "isFlt")
	private boolean error;
	@JacksonXmlProperty(localName = "flags")
	private String flags;
	@JacksonXmlProperty(localName = "lat")
	private String latitude;
	@JacksonXmlProperty(localName = "lon")
	private String longitude;
	@JacksonXmlProperty(localName = "heading")
	private String heading;
	
	public void setGenerated(String generated){
		this.generated = CTAUtil.parseDateTime(generated);
	}
	
	public void setArrivalTime(String arrivalTime){
		this.arrivalTime = CTAUtil.parseDateTime(arrivalTime);
	}

	public void setIsApproching(int isApproching) {
		if(isApproching == 0){
			this.isApproching = false;
		}else{
			this.isApproching = true;
		}
	}

	public void setIsScheduled(int isScheduled) {
		if(isScheduled == 0){
			this.isScheduled = false;
		}else{
			this.isScheduled = true;
		}
	}

	public void setIsDelayed(int isDelayed) {
		if(isDelayed == 0){
			this.isDelayed = false;
		}else{
			this.isDelayed = true;
		}
	}
	
	public void setError(int error){
		if(error == 0){
			this.error = false;
		}else{
			this.error = true;
		}
	}
	
	public JSONObject toJSON(){
		JSONObject json = new JSONObject();
		json.put("stationId", this.stationId);
		json.put("stopId", this.stopId);
		json.put("stationName", this.stationName);
		json.put("stationDesc", this.stationDesc);
		json.put("runNumber", this.runNumber);
		json.put("route", this.route);
		json.put("destinationSt", this.destinationSt);
		json.put("destinationName", this.destinationName);
		json.put("direction", this.direction);
		json.put("generated", this.generated.toDateTimeISO());
		json.put("arrivalTime", this.arrivalTime.toDateTimeISO());
		json.put("isApproching", this.isApproching);
		json.put("isScheduled", this.isScheduled);
		json.put("isDelayed", this.isDelayed);
		json.put("error", this.error);
		json.put("flags", this.flags);
		json.put("latitude", this.latitude);
		json.put("longitude", this.longitude);
		json.put("heading", this.heading);
		return json;
	}
}
