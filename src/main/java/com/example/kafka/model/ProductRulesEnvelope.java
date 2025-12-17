package com.example.kafka.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * ProductRulesEnvelope POJO for Avro serialization.
 * Wraps a collection of ProductRule objects.
 */
public class ProductRulesEnvelope implements Serializable {
    private List<ProductRule> products;

    public ProductRulesEnvelope() {
        this.products = new ArrayList<>();
    }

    public ProductRulesEnvelope(List<ProductRule> products) {
        this.products = products != null ? new ArrayList<>(products) : new ArrayList<>();
    }

    public List<ProductRule> getProducts() {
        return products;
    }

    public void setProducts(List<ProductRule> products) {
        this.products = products != null ? new ArrayList<>(products) : new ArrayList<>();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProductRulesEnvelope that = (ProductRulesEnvelope) o;
        return Objects.equals(products, that.products);
    }

    @Override
    public int hashCode() {
        return Objects.hash(products);
    }

    @Override
    public String toString() {
        return "ProductRulesEnvelope{" +
                "products=" + products +
                '}';
    }
}
