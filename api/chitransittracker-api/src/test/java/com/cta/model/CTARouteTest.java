package com.cta.model;

import static org.fest.assertions.api.Assertions.assertThat;

import java.net.URISyntaxException;

import org.apache.commons.lang.StringUtils;
import org.apache.http.client.utils.URIBuilder;
import org.junit.Test;

public class CTARouteTest {
	@Test
	public void testCTARouteToString() throws URISyntaxException{
		String[] mockAttributes = new String[7];
		mockAttributes[0] = "mock route";
		mockAttributes[1] = "#FF000";
		mockAttributes[2] = "#FFFFF";
		mockAttributes[3] = "0";
		URIBuilder uri = new URIBuilder("http://mock.me");
		mockAttributes[4] = uri.build().toString();
		mockAttributes[5] = "Normal";
		mockAttributes[6] = "#FFFFF";
		CTARoute mockRoute = new CTARoute();
		assertThat(mockRoute.toString()).isEqualTo(StringUtils.join(mockAttributes, ','));
	}
}