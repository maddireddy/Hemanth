package com.pnc.efg.dto;

import java.util.List;

public class ItemDto {
    private String product;
    // existing shape: list of rules
    private List<String> rules;
    // incoming shape from your payloads: single rule name per item
    private String ruleName;

    public ItemDto() { }

    public ItemDto(String product, List<String> rules) {
        this.product = product;
        this.rules = rules;
    }

    public ItemDto(String product, String ruleName) {
        this.product = product;
        this.ruleName = ruleName;
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
        this.rules = rules;
    }

    public String getRuleName() {
        return ruleName;
    }

    public void setRuleName(String ruleName) {
        this.ruleName = ruleName;
    }
}
