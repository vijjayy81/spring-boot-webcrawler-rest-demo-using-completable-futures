package org.vijjayy.demo.springframework.boot.webcrawler.controller;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.ResourceUtils;
import org.vijjayy.demo.springframework.boot.webcrawler.service.OutboundGatewayService;
import org.vijjayy.demo.springframework.boot.webcrawler.v1.api.model.ApiModelCrawlerNode;

import com.jayway.restassured.RestAssured;
import com.jayway.restassured.response.Response;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class CrawlerApiControllerTest {

	@Value("${local.server.port}")
	private int port;

	@MockBean
	private OutboundGatewayService outboundGatewayService;

	@Before
	public void setUpMocks() throws IOException {
		Mockito.when(outboundGatewayService.downloadHtmlContents("http://example.com"))
				.thenReturn(getHtmlContentFromMockFiles("classpath:mocks/base.html"));
		Mockito.when(outboundGatewayService.downloadHtmlContents("http://www.example.org/depth/level1-1"))
				.thenReturn(getHtmlContentFromMockFiles("classpath:mocks/level1-1.html"));
		Mockito.when(outboundGatewayService.downloadHtmlContents("http://www.example.org/depth/level1-2"))
				.thenReturn(getHtmlContentFromMockFiles("classpath:mocks/level1-2.html"));
		Mockito.when(outboundGatewayService.downloadHtmlContents("http://www.example.org/depth/level1-3"))
				.thenReturn(getHtmlContentFromMockFiles("classpath:mocks/level1-3.html"));
		Mockito.when(outboundGatewayService.downloadHtmlContents("http://www.example.org/depth/level2-1"))
				.thenReturn(getHtmlContentFromMockFiles("classpath:mocks/level2-1.html"));
		Mockito.when(outboundGatewayService.downloadHtmlContents("http://www.example.org/depth/level2-2"))
				.thenReturn(getHtmlContentFromMockFiles("classpath:mocks/level2-2.html"));
		Mockito.when(outboundGatewayService.downloadHtmlContents("http://www.example.org/depth/level2-3"))
				.thenReturn(getHtmlContentFromMockFiles("classpath:mocks/level2-3.html"));
		Mockito.when(outboundGatewayService.downloadHtmlContents("http://www.example.org/depth/level2-4"))
				.thenReturn(getHtmlContentFromMockFiles("classpath:mocks/level2-4.html"));
		Mockito.when(outboundGatewayService.downloadHtmlContents("http://www.example.org/depth/level2-5"))
				.thenReturn(getHtmlContentFromMockFiles("classpath:mocks/level2-5.html"));
		Mockito.when(outboundGatewayService.downloadHtmlContents("http://www.example.org/depth/level3-1"))
				.thenReturn(getHtmlContentFromMockFiles("classpath:mocks/level3-1.html"));
		Mockito.when(outboundGatewayService.downloadHtmlContents("http://www.example.org/depth/level3-2"))
				.thenReturn(getHtmlContentFromMockFiles("classpath:mocks/level3-2.html"));
		Mockito.when(outboundGatewayService.downloadHtmlContents("http://www.example.org/depth/level4-1"))
				.thenReturn(getHtmlContentFromMockFiles("classpath:mocks/level4-1.html"));

		Mockito.when(outboundGatewayService.downloadHtmlContents("http://www.example.org/pagenotfound")).thenThrow(
				new org.jsoup.HttpStatusException("Page not found", 400, "http://www.example.org/pagenotfound"));

	}

	@Test
	public void shouldSuccess() {

		Response response = RestAssured.given().port(port).when().get("/crawl?depth=5&breadth=5&url=http://example.com")
				.peek();

		Assert.assertEquals(200, response.getStatusCode());

		ApiModelCrawlerNode responseObj = response.getBody().as(ApiModelCrawlerNode.class);

		assertNotNull(responseObj);

		// Form mappings for assertions
		Map<String, ApiModelCrawlerNode> urlAndNodeMappings = getMappings(responseObj);

		// Check depth 4 object exists
		assertNotNull(urlAndNodeMappings.get("http://www.example.org/depth/level4-1"));

		// Check child node size
		assertEquals(3, urlAndNodeMappings.get("http://example.com").getNodes().size());
		assertEquals(0, urlAndNodeMappings.get("http://www.example.org/depth/level1-1").getNodes().size());
		assertEquals(1, urlAndNodeMappings.get("http://www.example.org/depth/level1-2").getNodes().size());
		assertEquals(1, urlAndNodeMappings.get("http://www.example.org/depth/level1-3").getNodes().size());
		assertEquals(0, urlAndNodeMappings.get("http://www.example.org/depth/level2-1").getNodes().size());
		assertEquals(0, urlAndNodeMappings.get("http://www.example.org/depth/level2-2").getNodes().size());
		assertEquals(0, urlAndNodeMappings.get("http://www.example.org/depth/level2-2").getNodes().size());
		assertEquals(1, urlAndNodeMappings.get("http://www.example.org/depth/level2-3").getNodes().size());
		assertNull(urlAndNodeMappings.get("http://www.example.org/depth/level2-4"));
		assertNull(urlAndNodeMappings.get("http://www.example.org/depth/level2-5"));
		assertEquals(2, urlAndNodeMappings.get("http://www.example.org/depth/level3-1").getNodes().size());

	}

	@Test
	public void shouldFailAsBadRequestAsNoURLParamPassed() {

		Response response = RestAssured.given().port(port).when().get("/crawl?depth=5&breadth=5").peek();

		Assert.assertEquals(400, response.getStatusCode());

	}

	@Test
	public void shouldPassWithBrokenLinks() {

		Response response = RestAssured.given().port(port).when().get("/crawl?depth=5&breadth=5&url=http://www.example.org/pagenotfound").peek();

		Assert.assertEquals(200, response.getStatusCode());

	}

	private void traverse(Map<String, ApiModelCrawlerNode> map, ApiModelCrawlerNode node) {
		map.put(node.getUrl(), node);
		node.getNodes().stream().forEach(n -> traverse(map, n));

	}

	private Map<String, ApiModelCrawlerNode> getMappings(ApiModelCrawlerNode result) {
		Map<String, ApiModelCrawlerNode> map = new ConcurrentHashMap<>();
		traverse(map, result);
		return map;
	}

	private String getHtmlContentFromMockFiles(String classpathFileLocation) {
		try {
			return IOUtils.toString(ResourceUtils.toURI(classpathFileLocation), Charset.defaultCharset());
		} catch (Exception e) {
			throw new RuntimeException("Mock file retrival failed with exception", e);
		}
	}

}
