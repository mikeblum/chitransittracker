package com.cta.util;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;

import org.apache.commons.io.FileUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mockito;

import com.cta.model.CTARoutes;


public class CTAXmlParserTest {
	HttpClient mockClient;
	HttpGet mockGet;
	HttpResponse mockResponse;
	HttpEntity mockEntity;
	InputStream mockXml;
	
	@Before
	public void setUp() throws ClientProtocolException, IOException {
		mockClient = mock(HttpClient.class);
		
		CTAXmlParser.client = mockClient;
		
		mockGet = mock(HttpGet.class);
		mockResponse = mock(HttpResponse.class);
		mockEntity = mock(HttpEntity.class);
		Mockito.when(mockClient.execute(Matchers.any(HttpGet.class))).thenReturn(mockResponse);
		Mockito.when(mockResponse.getEntity()).thenReturn(mockEntity);
			
		CTAXmlParser.USER_AGENT = "Mozilla/5.0";
		CTAXmlParser.RAIL = "rail";
		CTAXmlParser.BUS = "bus";
		CTAXmlParser.STATION = "station";
		CTAXmlParser.SCHEME = "http";
		CTAXmlParser.BASE = "lapi.transitchicago.com/api/";
		CTAXmlParser.VERSION = "1.0";
		CTAXmlParser.ROUTES = "routes.aspx";
	}
	
	@Test
	public void testCTABaseUrl(){
		String expectedUrl = "http://lapi.transitchicago.com/api/";
		try {
			String builtUrl = CTAXmlParser.getAPIBase().build().toString();
			assertThat(builtUrl, is(equalTo(expectedUrl)));
		} catch (URISyntaxException e) {
			fail("API Base URI build failure");
		}
	}
	
	@Test
	public void testGetTrainLinesURI(){
		String expectedUrl = "http://lapi.transitchicago.com/api/1.0/routes.aspx?type=" + CTAXmlParser.RAIL;
		assertThat(CTAXmlParser.getCTARoutesURI("rail").toString(), is(equalTo(expectedUrl)));
	}
	
	@Test
	public void testDeserializingTrainLines() throws IOException{
		mockXml = FileUtils.openInputStream(new File("src/test/resources/train_lines.xml"));
		Mockito.when(mockEntity.getContent()).thenReturn(mockXml);
		CTARoutes mockTrainLines = CTAXmlParser.getCTARoutesInfo(CTAXmlParser.RAIL);
		//9 cta train lines
		assertThat(mockTrainLines.getRoutes().size(), is(equalTo(9)));
	}
	
	@Test
	public void testGetTrainStationsURI(){
		String expectedUrl = "http://lapi.transitchicago.com/api/1.0/routes.aspx?type=" + CTAXmlParser.STATION;
		assertThat(CTAXmlParser.getCTARoutesURI(CTAXmlParser.STATION).toString(), is(equalTo(expectedUrl)));
	}
	
	@Test
	public void testDeserializingTrainStations() throws IOException{
		mockXml = FileUtils.openInputStream(new File("src/test/resources/stations.xml"));
		Mockito.when(mockEntity.getContent()).thenReturn(mockXml);
		CTARoutes mockTrainStations = CTAXmlParser.getCTARoutesInfo(CTAXmlParser.STATION);
		//209 cta train stations
		assertThat(mockTrainStations.getRoutes().size(), is(equalTo(209)));
	}
	
	@Test
	public void testGetBusesURI(){
		String expectedUrl = "http://lapi.transitchicago.com/api/1.0/routes.aspx?type=" + CTAXmlParser.BUS;
		assertThat(CTAXmlParser.getCTARoutesURI(CTAXmlParser.BUS).toString(), is(equalTo(expectedUrl)));
	}
	

	public void testDeserializingBusStops(){
		CTARoutes mockTrainStations = CTAXmlParser.getCTARoutesInfo(CTAXmlParser.BUS);
		//209 cta train stations
		assertThat(mockTrainStations.getRoutes().size(), is(equalTo(209)));
	}
}
