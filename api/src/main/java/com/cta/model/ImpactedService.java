package com.cta.model;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

/**
 * CTA Service affected by a CTAAlert
 * @author mblum
 *
 */
public class ImpactedService {
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
