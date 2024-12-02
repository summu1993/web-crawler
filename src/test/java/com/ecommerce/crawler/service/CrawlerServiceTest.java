package com.ecommerce.crawler.service;

import com.ecommerce.crawler.model.DomainResult;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CrawlerServiceTest {

    private CrawlerService crawlerService;

    @Mock
    private URLFilterService urlFilterService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        crawlerService = new CrawlerService(urlFilterService);
    }

    @Test
    void testCrawlDomain_withValidProductURLs() {
        when(urlFilterService.isValidProductURL(anyString())).thenReturn(true);

        String domain = "http://example.com";
        Document mockDocument = Jsoup.parse("""
                <html>
                    <body>
                        <a href="http://example.com/product/1">Product 1</a>
                        <a href="http://example.com/product/2">Product 2</a>
                        <a href="http://example.com/about">About</a>
                    </body>
                </html>
                """);

        CrawlerService spyService = spy(crawlerService);
        doReturn(mockDocument).when(spyService).fetchPage(anyString());

        DomainResult result = spyService.crawlDomain(domain);

        assertNotNull(result);
        assertEquals(domain, result.getDomain());
        assertEquals(Set.of(
                "http://example.com/product/1",
                "http://example.com/product/2"
        ), result.getProductUrls());
    }

    @Test
    void testCrawlDomain_withInvalidURLs() {
        String domain = "http://example.com";
        Document mockDocument = Jsoup.parse("""
                <html>
                    <body>
                        <a href="http://example.com/about">About</a>
                        <a href="http://example.com/contact">Contact</a>
                    </body>
                </html>
                """);

        when(urlFilterService.isValidProductURL(anyString())).thenReturn(false);

        CrawlerService spyService = spy(crawlerService);
        doReturn(mockDocument).when(spyService).fetchPage(anyString());

        DomainResult result = spyService.crawlDomain(domain);

        assertNotNull(result);
        assertTrue(result.getProductUrls().isEmpty());
    }

    @Test
    void testCrawlDomain_handlesNextPage() {
        String domain = "http://example.com";
        Document page1 = Jsoup.parse("""
                <html>
                    <body>
                        <a href="http://example.com/product/1">Product 1</a>
                        <a href="http://example.com/page2">Next</a>
                    </body>
                </html>
                """);
        Document page2 = Jsoup.parse("""
                <html>
                    <body>
                        <a href="http://example.com/product/2">Product 2</a>
                    </body>
                </html>
                """);

        when(urlFilterService.isValidProductURL(anyString())).thenReturn(true);

        CrawlerService spyService = spy(crawlerService);
        doReturn(page1).when(spyService).fetchPage(domain);
        doReturn(page2).when(spyService).fetchPage("http://example.com/page2");

        DomainResult result = spyService.crawlDomain(domain);

        assertNotNull(result);
        assertEquals(Set.of(
                "http://example.com/product/1",
                "http://example.com/product/2"
        ), result.getProductUrls());
    }
}