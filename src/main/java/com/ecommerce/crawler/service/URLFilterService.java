package com.ecommerce.crawler.service;

import java.util.List;
import java.util.regex.Pattern;

public class URLFilterService {

    private static final List<Pattern> PRODUCT_PATTERNS = List.of(
            Pattern.compile(".*/(product|item|p)/.*"),
            Pattern.compile(".*/dp/.*"), // Amazon-style patterns
            Pattern.compile(".*/goods/.*") // AliExpress-style patterns
    );

    public boolean isValidProductURL(String url) {
        return PRODUCT_PATTERNS.stream().anyMatch(pattern -> pattern.matcher(url).matches());
    }
}
