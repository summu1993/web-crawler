package com.ecommerce.crawler.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;

class URLFilterServiceTest {

    private URLFilterService urlFilterService;

    @BeforeEach
    void setUp() {
        urlFilterService = new URLFilterService();
    }

    @Test
    void testIsProductUrl_validProductUrls() {
        // Arrange
        List<String> validUrls = List.of(
                "https://example.com/product/12345",
                "https://example.com/item/67890",
                "https://example.com/p/11223"
        );

        // Act & Assert
        for (String url : validUrls) {
            assertTrue(urlFilterService.isValidProductURL(url), "Expected valid product URL: " + url);
        }
    }

    @Test
    void testIsProductUrl_invalidUrls() {
        // Arrange
        List<String> invalidUrls = List.of(
                "https://example.com/about",
                "https://example.com/contact",
                "https://example.com/blog"
        );

        // Act & Assert
        for (String url : invalidUrls) {
            assertFalse(urlFilterService.isValidProductURL(url), "Expected invalid product URL: " + url);
        }
    }

    @Test
    void testFilterProductUrls_combinedUrls() {
        // Arrange
        List<String> urls = List.of(
                "https://example.com/product/12345",
                "https://example.com/item/67890",
                "https://example.com/p/11223",
                "https://example.com/about",
                "https://example.com/contact"
        );

        // Act
        Set<String> productUrls = urls.stream()
                .filter(urlFilterService::isValidProductURL)
                .collect(Collectors.toSet());

        // Assert
        assertTrue(productUrls.contains("https://example.com/product/12345"));
        assertTrue(productUrls.contains("https://example.com/item/67890"));
        assertTrue(productUrls.contains("https://example.com/p/11223"));
        assertFalse(productUrls.contains("https://example.com/about"));
        assertFalse(productUrls.contains("https://example.com/contact"));
    }

    @Test
    void testIsProductUrl_emptyUrl() {
        // Arrange
        String emptyUrl = "";

        // Act
        boolean result = urlFilterService.isValidProductURL(emptyUrl);

        // Assert
        assertFalse(result, "Expected empty URL to be invalid");
    }
}