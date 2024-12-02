package com.ecommerce.crawler.service;

import com.ecommerce.crawler.model.DomainResult;
import com.ecommerce.crawler.model.ProductURL;
import com.ecommerce.crawler.util.URLUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

public class CrawlerService {

    private static final int THREAD_COUNT = 10;

    private final URLFilterService urlFilterService;

    public CrawlerService(URLFilterService urlFilterService) {
        this.urlFilterService = urlFilterService;
    }

    public void crawlDomains(List<String> domains) {
        ExecutorService executor = Executors.newFixedThreadPool(THREAD_COUNT);
        List<CompletableFuture<DomainResult>> futures = new ArrayList<>();

        for (String domain : domains) {
            futures.add(CompletableFuture.supplyAsync(() -> crawlDomain(domain), executor));
        }

        List<DomainResult> results = futures.stream()
                .map(CompletableFuture::join)
                .filter(Objects::nonNull)
                .toList();

        results.forEach(System.out::println);
        executor.shutdown();
    }

    DomainResult crawlDomain(String domain) {
        Set<String> visitedUrls = new HashSet<>();
        Queue<String> queue = new LinkedList<>();
        queue.add(domain);

        List<ProductURL> productUrls = new ArrayList<>();

        while (!queue.isEmpty()) {
            String url = queue.poll();

            if (!visitedUrls.add(url)) {
                continue; // Skip already visited URLs
            }

            Document doc = fetchPage(url);
            if (doc == null) continue;

            Elements links = doc.select("a[href]");
            for (Element link : links) {
                String nextUrl = link.attr("abs:href");

                if (urlFilterService.isValidProductURL(nextUrl)) {
                    productUrls.add(extractProductInfo(doc, nextUrl));
                } else if (URLUtils.isSameDomain(domain, nextUrl)) {
                    queue.add(nextUrl);
                }
            }

            // Handle infinite scrolling (example logic for pagination)
            String nextPageUrl = extractNextPageURL(doc);
            if (nextPageUrl != null && visitedUrls.add(nextPageUrl)) {
                queue.add(nextPageUrl);
            }
        }

        // Convert List<ProductURL> to Set<String> (product URLs)
        Set<String> productUrlSet = productUrls.stream()
                .map(ProductURL::getUrl) // Extract URLs from ProductURL objects
                .collect(Collectors.toSet());

        return new DomainResult(domain, productUrlSet);
    }

    Document fetchPage(String url) {
        try {
            return Jsoup.connect(url)
                    .userAgent("Mozilla/5.0")
                    .timeout(10000)
                    .get();
        } catch (IOException e) {
            System.err.println("Error fetching content from: " + url + " - " + e.getMessage());
            return null;
        }
    }

    private ProductURL extractProductInfo(Document doc, String url) {
        String name = doc.select("meta[property=og:title]").attr("content");
        String price = doc.select(".product-price").text(); // Adjust selector based on structure
        return new ProductURL(url, name.isEmpty() ? "N/A" : name, price.isEmpty() ? "N/A" : price);
    }

    private String extractNextPageURL(Document doc) {
        Element nextPage = doc.selectFirst("a[rel=next]");
        return nextPage != null ? nextPage.attr("abs:href") : null;
    }
}
