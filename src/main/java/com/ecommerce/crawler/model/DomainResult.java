package com.ecommerce.crawler.model;

import java.util.Set;

public class DomainResult {
    private final String domain;
    private final Set<String> productUrls;

    public DomainResult(String domain, Set<String> productUrls) {
        this.domain = domain;
        this.productUrls = productUrls;
    }

    public String getDomain() {
        return domain;
    }

    public Set<String> getProductUrls() {
        return productUrls;
    }

    @Override
    public String toString() {
        return "Domain: " + domain + "\nProduct URLs: " + productUrls;
    }
}
