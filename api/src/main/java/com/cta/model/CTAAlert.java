package com.cta.model;

import java.net.URI;

import org.joda.time.DateTime;

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
	private int tbd;
	@JacksonXmlProperty(localName = "MajorAlert")
	private int majorAlert;
	@JacksonXmlProperty(localName = "AlertURL")
	private URI AlertURL;
	
	//this alert impacts the following service:
	@JacksonXmlProperty(localName = "ImpactedService")
	//unwrapping is important since the RouteInfo objects have no wrapper
	@JacksonXmlElementWrapper(useWrapping = false)
	private ImpactedService impactedService;
	
	@JacksonXmlProperty(localName = "ttim")
	private int ttim;
	@JacksonXmlProperty(localName = "GUID")
	private String guid;
}
