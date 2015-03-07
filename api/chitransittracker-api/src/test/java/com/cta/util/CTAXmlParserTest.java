package com.cta.util;

import static org.fest.assertions.api.Assertions.assertThat;
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

import com.cta.model.CTAAlerts;
import com.cta.model.CTAArrivals;
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
		
		CTAXmlParser.setHttpClient(mockClient);
		
		mockGet = mock(HttpGet.class);
		mockResponse = mock(HttpResponse.class);
		mockEntity = mock(HttpEntity.class);
		Mockito.when(mockClient.execute(Matchers.any(HttpGet.class))).thenReturn(mockResponse);
		Mockito.when(mockResponse.getEntity()).thenReturn(mockEntity);
		//mimick the normally spring injected values
		CTAUtil.user_agent = "Mozilla/5.0";
		CTAUtil.RAIL = "rail";
		CTAUtil.BUS = "bus";
		CTAUtil.STATION = "station";
		CTAUtil.scheme = "http";
		CTAUtil.base = "lapi.transitchicago.com/api/";
		CTAUtil.version = "1.0";
		CTAUtil.routes = "routes.aspx";
		CTAUtil.alerts = "alerts.aspx";
	}
	
	@Test
	public void testCTABaseUrl(){
		String expectedUrl = "http://lapi.transitchicago.com/api/";
		try {
			String builtUrl = CTAUtil.getAPIBase().build().toString();
			assertThat(builtUrl).isEqualTo(expectedUrl);
		} catch (URISyntaxException e) {
			fail("API Base URI build failure");
		}
	}
	
	@Test
	public void testGetTrainLinesURI(){
		String expectedUrl = "http://lapi.transitchicago.com/api/1.0/routes.aspx?type=" + CTAUtil.RAIL;
		assertThat(CTAUtil.getCTARoutesURI("rail").toString()).isEqualTo(expectedUrl);
	}
	
	@Test
	public void testDeserializingTrainLines() throws IOException{
		mockXml = FileUtils.openInputStream(new File("src/test/resources/train_lines.xml"));
		Mockito.when(mockEntity.getContent()).thenReturn(mockXml);
		CTARoutes mockTrainLines = CTAXmlParser.getCTARoutesInfo(CTAUtil.RAIL);
		//9 cta train lines
		assertThat(mockTrainLines.getRoutes().size()).isEqualTo(9);
	}
	
	@Test
	public void testGetTrainStationsURI(){
		String expectedUrl = "http://lapi.transitchicago.com/api/1.0/routes.aspx?type=" + CTAUtil.STATION;
		assertThat(CTAUtil.getCTARoutesURI(CTAUtil.STATION).toString()).isEqualTo(expectedUrl);
	}
	
	@Test
	public void testDeserializingTrainStations() throws IOException{
		mockXml = FileUtils.openInputStream(new File("src/test/resources/stations.xml"));
		Mockito.when(mockEntity.getContent()).thenReturn(mockXml);
		CTARoutes mockTrainStations = CTAXmlParser.getCTARoutesInfo(CTAUtil.STATION);
		//209 cta train stations
		assertThat(mockTrainStations.getRoutes().size()).isEqualTo(209);
	}
	
	@Test
	public void testGetBusesURI(){
		String expectedUrl = "http://lapi.transitchicago.com/api/1.0/routes.aspx?type=" + CTAUtil.BUS;
		assertThat(CTAUtil.getCTARoutesURI(CTAUtil.BUS).toString()).isEqualTo(expectedUrl);
	}
	
	@Test
	public void testDeserializingBusStops() throws IOException{
		mockXml = FileUtils.openInputStream(new File("src/test/resources/bus_routes.xml"));
		Mockito.when(mockEntity.getContent()).thenReturn(mockXml);
		CTARoutes mockBusRoutes = CTAXmlParser.getCTARoutesInfo(CTAUtil.BUS);
		//209 cta train stations
		assertThat(mockBusRoutes.getRoutes().size()).isEqualTo(128);
	}
	
	@Test
	public void testGetAlertsURI(){
		String expectedUrl = "http://lapi.transitchicago.com/api/1.0/alerts.aspx";
		assertThat(CTAUtil.getCTAAlertsURI().toString()).isEqualTo(expectedUrl);
	}
	
	@Test
	public void testDeserializingAlerts() throws IOException{
		mockXml = FileUtils.openInputStream(new File("src/test/resources/alerts.xml"));
		Mockito.when(mockEntity.getContent()).thenReturn(mockXml);
		CTAAlerts mockAlerts = CTAXmlParser.getCTAAlerts();
		//alerts
		assertThat(mockAlerts.getAlerts().size()).isEqualTo(31);
	}
	
	@Test
	public void testDeserializingArrivals() throws IOException{
		mockXml = FileUtils.openInputStream(new File("src/test/resources/arrivals.xml"));
		Mockito.when(mockEntity.getContent()).thenReturn(mockXml);
		String mockStationId = "12345";
		CTAArrivals mockArrivals = CTAXmlParser.getCTAArrivals("1234", "5");
		//4 predictions
		assertThat(mockArrivals.getArrivals().size()).isEqualTo(4);
	}

}
