package com.ecommerce.crawler.util;

import java.net.MalformedURLException;
import java.net.URL;

public class URLUtils {

    public static boolean isSameDomain(String domain, String url) {
        try {
            URL domainUrl = new URL(domain);
            URL targetUrl = new URL(url);
            return domainUrl.getHost().equals(targetUrl.getHost());
        } catch (MalformedURLException e) {
            return false;
        }
    }
}
