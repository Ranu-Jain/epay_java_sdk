package com.sbi.epay.entity;

import org.json.JSONObject;

import java.util.Date;

public abstract class Entity {

    private final JSONObject modelJson;

    Entity(JSONObject jsonObject) {
        this.modelJson = jsonObject;
    }

    public <T> T get(String key) {
        // Return null if key not in JSONObject
        if (!has(key)) {
            return null;
        }
        // Return Date for timestamps
        String CREATED_AT = "created_at";
        String CAPTURED_AT = "captured_at";
        if (key.equals(CREATED_AT) || key.equals(CAPTURED_AT)) {
            return (T) new Date(modelJson.getLong(key) * 1000);
        }
        Object value = modelJson.get(key);
        if (value == null) {
            return null;
        }
        return (T) value.getClass().cast(value);
    }

    public JSONObject toJson() {
        return modelJson;
    }

    public boolean has(String key) {
        return modelJson.has(key);
    }

    public String toString() {
        return modelJson.toString();
    }
}
