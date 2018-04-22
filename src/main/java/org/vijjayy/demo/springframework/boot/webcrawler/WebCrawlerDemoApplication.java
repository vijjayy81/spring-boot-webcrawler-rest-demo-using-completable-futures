package org.vijjayy.demo.springframework.boot.webcrawler;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.cache.annotation.EnableCaching;

/**
 * 
 * Demo application for Web crawler
 * 
 * @author Vijjayy
 *
 */
@SpringBootApplication
@EnableCaching
public class WebCrawlerDemoApplication extends SpringBootServletInitializer {

	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		return application.sources(WebCrawlerDemoApplication.class);
	}

	public static void main(String... args) {

		SpringApplication.run(WebCrawlerDemoApplication.class, args); // NOSONAR - Return type
																		// (ConfigurableApplicationContext) which can't
																		// be closed
	}

}