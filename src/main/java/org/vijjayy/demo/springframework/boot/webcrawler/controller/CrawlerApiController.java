package org.vijjayy.demo.springframework.boot.webcrawler.controller;

import java.util.Optional;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.vijjayy.demo.springframework.boot.webcrawler.service.WebCrawlerProcessor;
import org.vijjayy.demo.springframework.boot.webcrawler.v1.api.CrawlerApi;
import org.vijjayy.demo.springframework.boot.webcrawler.v1.api.model.ApiModelCrawlerNode;

import io.swagger.annotations.ApiParam;

/**
 * Rest Controller implementing swagger spec codegen generated {@link CrawlerApi} 
 * 
 * 
 * @author Vijjayy
 *
 */
@RestController
public class CrawlerApiController implements CrawlerApi {

	private final Logger logger = LoggerFactory.getLogger(CrawlerApiController.class);
	
	private final WebCrawlerProcessor webCrawlerService;
	
	@Value("${webcrawler.default.depth.limit}")
	private int defaultDepth;
	
	@Value("${webcrawler.default.breadth.limit}")
	private int defaultBreadth;
	
	@Value("${webcrawler.default.depth.max.limit}")
	private int maxDepth;
	
	@Value("${webcrawler.default.breadth.max.limit}")
	private int maxBreadth;
	
	//Prefer Constructor injection
	public CrawlerApiController(WebCrawlerProcessor webCrawlerService) {
		super();
		this.webCrawlerService = webCrawlerService;
	}

	@Override
	public ResponseEntity<ApiModelCrawlerNode> getWebCrawler(
			@NotNull @ApiParam(value = "Query param for 'url'", required = true) @Valid @RequestParam(value = "url", required = true) String url,
			@ApiParam(value = "Query param for crawling depth level") @Valid @RequestParam(value = "depth", required = false) Integer depth,
			@ApiParam(value = "Query param for crawling breadth level") @Valid @RequestParam(value = "breadth", required = false) Integer breadth) {
		
		logger.debug("Entering getWebCrawler() with URL [{}]", url);
		
		final int effectiveDepth = Integer.min(Optional.ofNullable(depth).orElse(defaultDepth),
                maxDepth);

		final int effectiveBreadth = Integer.min(Optional.ofNullable(breadth).orElse(defaultBreadth),
                maxBreadth);
		
		ApiModelCrawlerNode result = webCrawlerService.getWebCrawlerResult(url, effectiveDepth, effectiveBreadth);
		
		logger.debug("Exiting getWebCrawler() for URL [{}]", url);
		
		return ResponseEntity.ok(result);
	}


}
