package com.cta.bus.model;

import java.util.List;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

/**
 * Since CTA Bus API doesn't spoort getting all possible stops for a route (say bus #2) i  all directions, 
 * seperate quests have to be made. This model serializes all the possible directions for a route.
 * @author mblum
 *
 */
@JacksonXmlRootElement(localName = "bustime-response")
public class CTADirections {
	//this route has the following directions
	@JacksonXmlProperty(localName = "dir")
	//unwrapping is important since each direction is a single line
	@JacksonXmlElementWrapper(useWrapping = false)
	private List<String> directions;
	
	public List<String> getDirections(){
		return this.directions;
	}
}
