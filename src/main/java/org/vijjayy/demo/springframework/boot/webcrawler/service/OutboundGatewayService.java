package org.vijjayy.demo.springframework.boot.webcrawler.service;

import java.io.IOException;

import org.jsoup.Jsoup;
import org.springframework.stereotype.Service;

/**
 * Outbound gateway to interact with Internet. This can hold Proxy config.
 * 
 * For JUnit this bean can be mocked easily so the application can be easily testable by JUnit test cases with mocks, hence this is isolated.
 * 
 * 
 * @author Vijjayy
 *
 */
@Service
public class OutboundGatewayService {

	public String downloadHtmlContents(String url) throws IOException {
		return Jsoup.connect(url).timeout(5000).get().toString();
	}
	
}
