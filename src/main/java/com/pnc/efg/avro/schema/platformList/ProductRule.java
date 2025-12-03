package com.pnc.efg.avro.schema.platformList;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ProductRule implements Serializable {
    private String product;
    private List<String> rules;

    public ProductRule() {
        this.rules = new ArrayList<>();
    }

    public ProductRule(String product, List<String> rules) {
        this.product = product;
        this.rules = rules != null ? new ArrayList<>(rules) : new ArrayList<>();
    }

    public String getProduct() {
        return product;
    }

    public void setProduct(String product) {
        this.product = product;
    }

    public List<String> getRules() {
        return rules;
    }

    public void setRules(List<String> rules) {
        this.rules = rules != null ? new ArrayList<>(rules) : new ArrayList<>();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProductRule that = (ProductRule) o;
        return Objects.equals(product, that.product) && Objects.equals(rules, that.rules);
    }

    @Override
    public int hashCode() {
        return Objects.hash(product, rules);
    }

    @Override
    public String toString() {
        return "ProductRule{" +
                "product='" + product + '\'' +
                ", rules=" + rules +
                '}';
    }
}
