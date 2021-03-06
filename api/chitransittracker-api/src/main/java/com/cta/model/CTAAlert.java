package com.cta.model;

import java.net.URI;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;

import com.cta.util.CTAUtil;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

public class CTAAlert {
	@JacksonXmlProperty(localName = "AlertId")
	private String alertId;
	@JacksonXmlProperty(localName = "Headline")
	private String headline;
	@JacksonXmlProperty(localName = "ShortDescription")
	private String shortDescription;
	@JacksonXmlProperty(localName = "FullDescription")
	private String fullDescription;
	@JacksonXmlProperty(localName = "SeverityScore")
	private int severityScore;
	@JacksonXmlProperty(localName = "SeverityColor")
	private String severityColor;
	@JacksonXmlProperty(localName = "SeverityCSS")
	private String severityCSS;
	@JacksonXmlProperty(localName = "Impact")
	private String impact;
	@JacksonXmlProperty(localName = "EventStart")
	private DateTime eventStart;
	@JacksonXmlProperty(localName = "EventEnd")
	private DateTime eventEnd;
	@JacksonXmlProperty(localName = "TBD")
	private String tbd;
	@JacksonXmlProperty(localName = "MajorAlert")
	private String majorAlert;
	@JacksonXmlProperty(localName = "AlertURL")
	private URI alertURL;
	
	//this alert impacts the following service:
	@JacksonXmlProperty(localName = "ImpactedService")
	//unwrapping is important since the RouteInfo objects have no wrapper
	@JacksonXmlElementWrapper(useWrapping = false)
	private CTAImpactedService impactedService;
	
	@JacksonXmlProperty(localName = "ttim")
	private int ttim;
	@JacksonXmlProperty(localName = "GUID")
	private String guid;
	
	public void setEventStart(String eventStart){
		this.eventStart = CTAUtil.parseDateTime(eventStart);
	}
	
	public void setEventEnd(String eventEnd){
		//issue with cta xml where eventEnd may be blank, the string N/A, or null
		if(eventEnd == null || StringUtils.equalsIgnoreCase(eventEnd, "N/A")){
			this.eventEnd = null;
		}else{
			this.eventEnd = CTAUtil.parseDateTime(eventEnd);
		}
	}
	
	public Object[] getAttributes(){
		Object[] attributes = new Object[14];
		attributes[0] = this.alertId;
		attributes[1] = this.headline;
		attributes[2] = this.shortDescription;
		attributes[3] = this.fullDescription;
		attributes[4] = this.severityScore;
		attributes[5] = this.severityColor;
		attributes[6] = this.severityCSS;
		attributes[7] = this.impact;
		attributes[8] = this.eventStart;
		attributes[9] = this.eventEnd;
		attributes[10] = this.tbd;
		attributes[11] = this.majorAlert;
		attributes[12] = this.alertURL.toString();
		//reference the service impacted by this alert
		attributes[13] = this.impactedService.getImpactedServices().get(0).getServiceId();
		
		return attributes;
	}
	
	/**
	 * get comma-seperated list of attributes
	 */
	public String toString(){
		return StringUtils.join(getAttributes(), ',');
	}
}
