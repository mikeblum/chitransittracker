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
import org.mockito.Mockito;

import com.cta.bus.model.CTABusDirections;
import com.cta.bus.model.CTABusStops;
import com.cta.model.CTAAlerts;
import com.cta.model.CTAArrivals;
import com.cta.model.CTARoutes;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;


public class CTAXmlParserTest {
	CTAXmlParser mockParser;
	HttpClient mockClient;
	HttpGet mockGet;
	HttpResponse mockResponse;
	HttpEntity mockEntity;
	InputStream mockXml;
	
	@Before
	public void setUp() throws ClientProtocolException, IOException {
		mockGet = mock(HttpGet.class);
		mockResponse = mock(HttpResponse.class);
		mockEntity = mock(HttpEntity.class);
		mockParser = Mockito.mock(CTAXmlParser.class);
		mockClient = Mockito.mock(HttpClient.class); 
		HttpResponse response = Mockito.mock(HttpResponse.class);
		
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
		XmlMapper xmlMapper = new XmlMapper();
		CTARoutes mockTrainLines = xmlMapper.readValue(mockXml, CTARoutes.class);
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
		XmlMapper xmlMapper = new XmlMapper();
		CTARoutes mockTrainStations = xmlMapper.readValue(mockXml, CTARoutes.class);
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
		XmlMapper xmlMapper = new XmlMapper();
		CTARoutes mockBusRoutes = xmlMapper.readValue(mockXml, CTARoutes.class);
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
		XmlMapper xmlMapper = new XmlMapper();
		CTAAlerts mockAlerts = xmlMapper.readValue(mockXml, CTAAlerts.class);
		assertThat(mockAlerts.getAlerts().size()).isEqualTo(31);
	}
	
	@Test
	public void testDeserializingArrivals() throws IOException{
		mockXml = FileUtils.openInputStream(new File("src/test/resources/arrivals.xml"));
		XmlMapper xmlMapper = new XmlMapper();
		CTAArrivals mockArrivals = xmlMapper.readValue(mockXml, CTAArrivals.class);
		//4 predictions
		assertThat(mockArrivals.getArrivals().size()).isEqualTo(4);
	}
	
	@Test
	public void testDeserialzingBusRouteDirections() throws IOException{
		mockXml = FileUtils.openInputStream(new File("src/test/resources/bus_directions.xml"));
		XmlMapper xmlMapper = new XmlMapper();
		CTABusDirections mockDirections = xmlMapper.readValue(mockXml, CTABusDirections.class);
		//2 directions
		assertThat(mockDirections.getDirections().size()).isEqualTo(2);
	}
	
	@Test
	public void testDeserialzingBusStops() throws IOException{
		mockXml = FileUtils.openInputStream(new File("src/test/resources/bus_stops.xml"));
		XmlMapper xmlMapper = new XmlMapper();
		CTABusStops mockBusStops = xmlMapper.readValue(mockXml, CTABusStops.class);
		//2 directions
		assertThat(mockBusStops.getBusStops().size()).isEqualTo(113);
	}

}
