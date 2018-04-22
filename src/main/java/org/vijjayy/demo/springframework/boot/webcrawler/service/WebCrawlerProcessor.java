package org.vijjayy.demo.springframework.boot.webcrawler.service;

import java.util.ArrayList;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.vijjayy.demo.springframework.boot.webcrawler.model.URLContentInfo;
import org.vijjayy.demo.springframework.boot.webcrawler.v1.api.model.ApiModelCrawlerNode;

/**
 * 
 * Crawler Service performs and handles depth crawling
 * 
 * @author Vijjayy
 *
 */
@Service
public class WebCrawlerProcessor {

	private final Logger logger = LoggerFactory.getLogger(WebCrawlerProcessor.class);

	/**
	 * No CPU intensive work, lots of IO, hence setup with 50 threads
	 * 
	 */
	private final Executor ioBoundExecutorService = Executors.newFixedThreadPool(50);

	private final DownloadPageContentService downloadPageContentService;
	
	// Prefer constructor injection
	public WebCrawlerProcessor(DownloadPageContentService downloadPageContentService) {
		super();
		this.downloadPageContentService = downloadPageContentService;
	}

	/**
	 * Base URL webpage will be crawled up to the level of depth specified with the
	 * breadth(No of link nodes for a page).
	 * 
	 * This limitation is for performance reason.
	 * 
	 * 
	 * @param baseURL
	 * @param depth
	 * @param breadth
	 * @return
	 */
	public ApiModelCrawlerNode getWebCrawlerResult(final String baseURL, final int depth, final int breadth) {

		logger.debug("Entering getWebCrawlerResult()");
		
		ApiModelCrawlerNode result = crawl(baseURL, depth, breadth, new ConcurrentLinkedQueue<String>());
		
		logger.debug("Exiting getWebCrawlerResult()");
		
		return result;
	}

	private ApiModelCrawlerNode crawl(final String url, final int depth, final int breadth,
			final ConcurrentLinkedQueue<String> alreadCrawledURLStore) {

		logger.debug("Entering crawl() with url [{}]", url);

		// Set default values, this will not throw any exception, exception gets handled gracefully and logged.
		ApiModelCrawlerNode thisParentNode = new ApiModelCrawlerNode();
		thisParentNode.setUrl(url);
		thisParentNode.setNodes(new ArrayList<>());//To display empty array instead of null in the response json
	
		//Depth check
		if (depth < 0) {
			thisParentNode.setTitle("No further page links processed as depth limit reached");
			return thisParentNode;
		}

		//Duplicate check
		if (alreadCrawledURLStore.contains(url)) {
			
			logger.debug("crawl() -> url [{}] already process as this may result cyclic loop", url);
			
			// Null here, later this will be removed from collection
			return null;
		}

		// Add the URL to prevent duplications
		alreadCrawledURLStore.add(url);

		// CompletableFuture with Page invocation and processing functions
		CompletableFuture
				.supplyAsync(() -> downloadPageContentService.downloadPageContents(url),
						ioBoundExecutorService)
				.thenApply(contentInfo -> applyTitleAndReturnLinks(contentInfo, thisParentNode, breadth)) // set title info on to the parent and limit the results by breadth
				.thenApply(crawlFurtherAndLinkToParentNode(depth, breadth, alreadCrawledURLStore,
						thisParentNode))    // For each page links fetch the details recursively and set to parent node
				.thenApply(futures -> futures.toArray(CompletableFuture[]::new)) //Combine all futures for the page links
				.thenAccept(CompletableFuture::allOf) // New completable future for all tasks to be finished
				.join(); //combine 



		logger.debug("Exiting crawl() with url [{}]", url);

		return thisParentNode;

	}

	private Set<String> applyTitleAndReturnLinks(final URLContentInfo contents, final ApiModelCrawlerNode parentNode,
			final int breadth) {
		
		//Set the title to the parent
		parentNode.setTitle(contents.getTitle());
		//Limit the page links by breadth limit
		return contents.getLinkNodes().stream().limit(breadth).collect(Collectors.toSet());
	}

	private Function<Set<String>, Stream<CompletableFuture<ApiModelCrawlerNode>>> crawlFurtherAndLinkToParentNode(
			final int depth, final int breadth, ConcurrentLinkedQueue<String> crawledPages,
			ApiModelCrawlerNode parentNode) {

		return pageLinks -> pageLinks.stream().map(pageLink -> {

			// Reduce depth by one and call crawl
			ApiModelCrawlerNode childNodeForThePageLink = crawl(pageLink, depth - 1, breadth, crawledPages);

			//This case can be true if duplicate page links are exists 
			//in the chain of links and future will complete with null and this is ignored in the parent node
			if (childNodeForThePageLink != null) {
				parentNode.addNodesItem(childNodeForThePageLink);
			}
			return CompletableFuture.completedFuture(childNodeForThePageLink);
		});

	}
	
}
