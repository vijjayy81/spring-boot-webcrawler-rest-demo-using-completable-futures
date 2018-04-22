package org.vijjayy.demo.springframework.boot.webcrawler.service;

import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.vijjayy.demo.springframework.boot.webcrawler.model.URLContentInfo;


/**
 * Service to fetch Page contents.
 * 
 * 
 * @author Vijjayy
 *
 */
@Service
public class DownloadPageContentService {
	
	private final Logger logger = LoggerFactory.getLogger(DownloadPageContentService.class);

	private final OutboundGatewayService outboundGatewayService;
	
	//Prefer constructor injection
	public DownloadPageContentService(OutboundGatewayService outboundGatewayService) {
		super();
		this.outboundGatewayService = outboundGatewayService;
	}



	/**
	 * 
	 * Cacheable service returns {@link URLContentInfo}
	 * 
	 * @param url
	 * @param breadth
	 * @return
	 */
	@Cacheable(cacheNames = "urlContentsCache", key = "#url")
	public URLContentInfo downloadPageContents(String url) {
		
		logger.debug("Entering downloadPageContents() with URL [{}]", url);
		
		
		try {
			URLContentInfo urlContent = new URLContentInfo();
			
			urlContent.setUrl(url);
			
			//Don't wait for more than 5 seconds
			Document document = Jsoup.parse(outboundGatewayService.downloadHtmlContents(url));
			
			//links
			Elements linksOnPage = document.select("a[href]");

			urlContent.setTitle(document.title());

			//Get absolute URL, Filter empty links
			urlContent.getLinkNodes()
					.addAll(linksOnPage.stream().filter(a -> StringUtils.isNotEmpty(a.attr("abs:href")))
							.map(a -> a.attr("abs:href")).collect(Collectors.toList()));

			logger.debug("Exiting downloadPageContents() for URL [{}]", url);
			
			return urlContent;
			
		} catch (Exception e) {
			URLContentInfo urlContent = new URLContentInfo();
			urlContent.setUrl("Broken link");
			urlContent.setTitle("Exception occured and title as exception message " + e.getMessage());
			logger.error("Exception while fetching page details", e);
			return urlContent;
		}
		
	}

}
