package com.cta.model;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

public class CTAService {
	@JacksonXmlProperty(localName = "ServiceType")
	private String serviceType;
	@JacksonXmlProperty(localName = "ServiceTypeDescription")
	private String serviceTypeDescription;
	@JacksonXmlProperty(localName = "ServiceName")
	private String serviceName;
	@JacksonXmlProperty(localName = "ServiceId")
	private String serviceId;
	@JacksonXmlProperty(localName = "ServiceBackColor")
	private String serviceBackColor;
	@JacksonXmlProperty(localName = "ServiceTextColor")
	private String ServiceTextColor;
	@JacksonXmlProperty(localName = "ServiceURL")
	private String serviceURL;
}
