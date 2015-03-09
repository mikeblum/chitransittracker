package com.cta.model;

import java.util.List;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;


/**
 * CTA Service affected by a CTAAlert
 * @author mblum
 *
 */
public class CTAImpactedService {
	@JacksonXmlProperty(localName = "Service")
	//unwrapping is important since the RouteInfo objects have no wrapper
	@JacksonXmlElementWrapper(useWrapping = false)
	List<CTAService> services;
	
	public List<CTAService> getImpactedServices(){
		return this.services;
	}
}
