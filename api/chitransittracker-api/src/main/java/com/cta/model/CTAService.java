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
	
	public String getServiceType() {
		return serviceType;
	}
	public String getServiceTypeDescription() {
		return serviceTypeDescription;
	}
	public String getServiceName() {
		return serviceName;
	}
	public String getServiceId() {
		return serviceId;
	}
	public String getServiceBackColor() {
		return serviceBackColor;
	}
	public String getServiceTextColor() {
		return ServiceTextColor;
	}
	public String getServiceURL() {
		return serviceURL;
	}
}
