package com.cta.util;

import static org.fest.assertions.api.Assertions.*;

import java.io.IOException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.junit.Before;
import org.junit.Test;

public class CTAPingTest {
	HttpClient client = null;
	@Before
	public void setUp(){
		client = HttpClientBuilder.create().build();
	}
	
	@Test
	public void testConnectingToCTAServers() throws ClientProtocolException, IOException{
		String systemHealthCheck = "http://lapi.transitchicago.com/api/1.0/routes.aspx?type=systemwide";
		HttpGet getSystemCheck = new HttpGet(systemHealthCheck);
		// add request header
		getSystemCheck.addHeader("User-Agent", "Mozilla/5.0");
		HttpResponse responseFromHealthCheck = client.execute(getSystemCheck);
		//expect something around a 200 response
		assertThat(responseFromHealthCheck.getStatusLine().getStatusCode()).isGreaterThanOrEqualTo(200).isLessThan(400);
	}
}
