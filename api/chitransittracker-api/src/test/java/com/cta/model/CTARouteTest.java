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
		mockRoute.setRouteName(mockAttributes[0]);
		mockRoute.setRouteColorCode(mockAttributes[1]);
		mockRoute.setRouteTextColor(mockAttributes[2]);
		mockRoute.setServiceId(mockAttributes[3]);
		mockRoute.setRouteUrl(uri.build());
		mockRoute.setRouteStatus(mockAttributes[5]);
		mockRoute.setRouteStatusColor(mockAttributes[6]);
		assertThat(mockRoute.toString()).isEqualTo(StringUtils.join(mockAttributes, ','));
	}
}