package com.ecommerce.crawler.model;

public class ProductURL {
    private final String url;
    private final String name;
    private final String price;

    public ProductURL(String url, String name, String price) {
        this.url = url;
        this.name = name;
        this.price = price;
    }

    public String getUrl() {
        return url;
    }

    public String getName() {
        return name;
    }

    public String getPrice() {
        return price;
    }

    @Override
    public String toString() {
        return "ProductURL{" +
                "url='" + url + '\'' +
                ", name='" + name + '\'' +
                ", price='" + price + '\'' +
                '}';
    }
}
