package com.cta.model;

import java.util.List;

import org.joda.time.DateTime;

import com.cta.util.CTAUtil;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

@JacksonXmlRootElement(localName = "ctatt")
public class CTAArrivals {
	@JacksonXmlProperty(localName = "tmst")
	DateTime timeStamp;
	@JacksonXmlProperty(localName = "errCd")
	String errCd;
	@JacksonXmlProperty(localName = "errNm")
	String errNm;
	//unwrapping is important since the Arrivals objects have no wrapper
	@JacksonXmlElementWrapper(useWrapping = false)
	@JacksonXmlProperty(localName = "eta")
	List<CTAArrival> arrivals;
	
	public List<CTAArrival> getArrivals(){
		return arrivals;
	}
	
	public void setArrivals(List<CTAArrival> arrivals){
		this.arrivals = arrivals;
	}
	
	public void setTimeStamp(String timeStamp) {
		this.timeStamp = CTAUtil.parseDateTime(timeStamp);
	}
}
