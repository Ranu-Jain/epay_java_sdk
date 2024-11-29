package com.sbi.epay.client;

import com.sbi.epay.entity.Entity;
import com.sbi.epay.exceptions.SBIEpayException;
import com.sbi.epay.utils.ApiUtils;
import com.sbi.epay.utils.EntityNameURLMapping;
import okhttp3.HttpUrl;
import okhttp3.Response;
import org.apache.commons.text.WordUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

public class ApiClient {

    private final int STATUS_OK = 200;
    private final int STATUS_MULTIPLE_CHOICE = 300;
    String auth;


    ApiClient(String auth) {
        this.auth = auth;
    }

    public <T extends Entity> T get(String version, String path, String service, JSONObject requestObject) throws SBIEpayException {
        Response response = ApiUtils.getRequest(version, path, service, requestObject, auth);
        return processResponse(response);
    }

    public <T> T post(String version, String path, String service, JSONObject requestObject) throws SBIEpayException {
        Response response = ApiUtils.postRequest(version, path, service, requestObject, auth);
        return processResponse(response);
    }

    <T extends Entity> ArrayList<T> getCollection(String version, String path, String service, JSONObject requestObject) throws SBIEpayException {
        Response response = ApiUtils.getRequest(version, path, service, requestObject, auth);
        return processCollectionResponse(response);
    }

    private <T extends Entity> T parseResponse(JSONObject jsonObject, String entity) throws SBIEpayException {
        if (entity != null) {
            Class<T> cls = getClass(entity);
            try {
                assert cls != null;
                return cls.getConstructor(JSONObject.class).newInstance(jsonObject);
            } catch (Exception e) {
                throw new SBIEpayException("Unable to parse response because of " + e.getMessage());
            }
        }

        throw new SBIEpayException("Unable to parse response");
    }

    private <T extends Entity> ArrayList<T> parseCollectionResponse(JSONArray jsonArray, HttpUrl requestUrl) throws SBIEpayException {

        ArrayList<T> modelList = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObj = jsonArray.getJSONObject(i);
            T t = parseResponse(jsonObj, getEntity(jsonObj, requestUrl));
            modelList.add(t);
        }
        return modelList;
    }

    /*
     * this method will take http url as : https://sbi.epay.api.com/v1/order
     * and will return entity name with the help of @EntityNameURLMapping class
     */
    private String getEntityNameFromURL(HttpUrl url) {
        String param = url.pathSegments().get(1);
        return EntityNameURLMapping.getEntityName(param);
    }


    <T> T processResponse(Response response) throws SBIEpayException {
        if (response == null) {
            throw new SBIEpayException("Invalid Response from server");
        }

        int statusCode = response.code();
        String responseBody;
        JSONObject responseJson;
        try {
            assert response.body() != null;
            responseBody = response.body().string();
            if (responseBody.equals("[]")) {
                return (T) Collections.emptyList();
            } else {
                responseJson = new JSONObject(responseBody);
            }
        } catch (IOException e) {
            throw new SBIEpayException(e.getMessage());
        }

        if (statusCode >= STATUS_OK && statusCode < STATUS_MULTIPLE_CHOICE) {
            return (T) parseResponse(responseJson, getEntity(responseJson, response.request().url()));
        }

        throwException(statusCode, responseJson);
        return null;
    }

    <T extends Entity> ArrayList<T> processCollectionResponse(Response response) throws SBIEpayException {
        if (response == null) {
            throw new SBIEpayException("Invalid Response from server");
        }

        int statusCode = response.code();
        String responseBody;
        JSONObject responseJson;

        try {
            assert response.body() != null;
            responseBody = response.body().string();
            responseJson = new JSONObject(responseBody);
        } catch (IOException e) {
            throw new SBIEpayException(e.getMessage());
        }

        String collectionName = responseJson.has("payment_links") ? "payment_links" : "items";

        if (statusCode >= STATUS_OK && statusCode < STATUS_MULTIPLE_CHOICE) {
            return parseCollectionResponse(responseJson.getJSONArray(collectionName), response.request().url());
        }

        throwException(statusCode, responseJson);
        return null;
    }

    private String getEntity(JSONObject jsonObj, HttpUrl url) {
        String ENTITY = "entity";
        if (!jsonObj.has(ENTITY)) {
            return getEntityNameFromURL(url);
        } else if (getClass(jsonObj.get("entity").toString()) == null) {
            return getEntityNameFromURL(url);
        } else {
            return jsonObj.getString(ENTITY);
        }
    }

    private void throwException(int statusCode, JSONObject responseJson) throws SBIEpayException {
        String ERROR = "error";
        if (responseJson.has(ERROR)) {
            JSONObject errorResponse = responseJson.getJSONObject(ERROR);
            String STATUS_CODE = "code";
            String code = errorResponse.getString(STATUS_CODE);
            String DESCRIPTION = "description";
            String description = errorResponse.getString(DESCRIPTION);
            throw new SBIEpayException(code + ":" + description);
        }
        throwServerException(statusCode, responseJson.toString());
    }

    private void throwServerException(int statusCode, String responseBody) throws SBIEpayException {
        throw new SBIEpayException("Status Code: " + statusCode + "\n" + "Server response: " + responseBody);
    }

    private Class getClass(String entity) {
        try {
            String entityClass = "com.sbi.epay." + WordUtils.capitalize(entity, '_').replaceAll("_", "");
            return Class.forName(entityClass);
        } catch (ClassNotFoundException e) {
            return null;
        }
    }
}