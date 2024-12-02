package com.ecommerce.crawler;

import com.ecommerce.crawler.service.CrawlerService;
import com.ecommerce.crawler.service.URLFilterService;

import java.util.List;

public class CrawlerApplication {
    public static void main(String[] args) {

        URLFilterService urlFilterService = new URLFilterService();
        CrawlerService crawlerService = new CrawlerService(urlFilterService);

        List<String> domains = List.of(
                "https://example1.com",
                "https://www.amazom.com",
                "https://www.flipkart.com"
        );
        crawlerService.crawlDomains(domains);
    }
}