package com.sbi.epay.utils;

import java.util.Arrays;

/**
 * Enum name is acting as url and entity is denoting Entity class
 * ex: <a href="https://sbi.epay.api.com/v1/order">...</a>
 * getEntityName method will take "order" from above-mentioned url
 * and will return "order" as entity name as mentioned below in mapping ORDER("order")
 */

public enum EntityNameURLMapping {

    ORDER("order"), CUSTOMER("customer"), TOKENS("token");
    private final String entity;

    EntityNameURLMapping(String entity) {
        this.entity = entity;
    }

    public static String getEntityName(String urlStirng) {
        EntityNameURLMapping item = Arrays.stream(values()).filter(val -> val.name().equalsIgnoreCase(urlStirng)).findFirst().orElseThrow(() -> new IllegalArgumentException("Unable to resolve"));
        return item.getEntity();
    }

    private String getEntity() {
        return entity;
    }
}